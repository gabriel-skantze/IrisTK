/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;

import iristk.system.IrisUtils;

/**
 * Works like an ArrayBlockingQueue but with bytes. This means that one thread may write bytes to the queue while another thread reads them, blocking if not enough bytes have been written. The capacity is dynamically adjusted.
 */
public class BlockingByteQueue {
	
	Logger logger = IrisUtils.getLogger(BlockingByteQueue.class);

	private byte[] buffer;
	
	private int writePos = 0;
	private int readPos = 0;
	private int overhead = 0;

	private boolean writing = true;
		
	public BlockingByteQueue() {
		buffer = new byte[1024];
		//buffer = new byte[100000];
	}
	
	public int capacity() {
		return buffer.length;
	}
	
	//public String name = null;
	
	/**
	 * Writes {@code len} bytes from {@code bytes}, starting at position {@code pos} to the queue
	 */
	public void write(byte[] bytes, int pos, int len) {
		synchronized (this) {
			//if  (name != null)
			//	System.out.println(this.name + " WRITING: writePos:" + writePos + " bufWritePos:" + bufWritePos + " readPos:" + readPos + " bufReadPos: " + bufReadPos);
			writing = true;
			if (overhead + len > capacity()) {
				//System.out.println(" EXPANDING: bufWritePos:" + writePos + " bufReadPos:" + readPos);
				int newCapacity = capacity() * 2;
				while (overhead + len > newCapacity) {
					newCapacity = newCapacity * 2;
				}
				//logger.info("New capacity: " + newCapacity);
				if (newCapacity > 1000000) {
					logger.warn("BlockingByteQueue growing to " + newCapacity);
				}
				byte[] oldBuffer = buffer;
				buffer = new byte[newCapacity];
				if (readPos > writePos) {
					int split = oldBuffer.length - readPos;
					System.arraycopy(oldBuffer, readPos, buffer, 0, split);
					System.arraycopy(oldBuffer, 0, buffer, split, writePos);
				} else {
					System.arraycopy(oldBuffer, readPos, buffer, 0, overhead);
				}
				writePos = overhead;
				readPos = 0;
				//System.out.println(" EXPANDED: bufWritePos:" + writePos + " newCapacity:" + newCapacity);
			}
			if (writePos + len > capacity()) {
				//if  (name != null)
				//System.out.println(" RECYCLING");
				// Reached the end of the buffer, restart at the beginning
				int split = capacity() - writePos;
				System.arraycopy(bytes, pos, buffer, writePos, split);
				System.arraycopy(bytes, pos + split, buffer, 0, len - split);
				writePos = len - split;
			} else {
				System.arraycopy(bytes, pos, buffer, writePos, len);
				writePos += len;
			}
			overhead += len;
			this.notify();
		}
	}

	/**
	 * Writes {@code bytes} to the queue
	 */
	public void write(byte[] bytes) {
		this.write(bytes, 0, bytes.length);
	}
	
	/**
	 * Ends the writing, which means that reading from the queue will no longer block. To restore the blocking, {@code reset()} muse be called.
	 */
	public void endWrite() {
		synchronized (this) {
			writing  = false;
			this.notify();
		}
	}

	public int read(byte[] bytes) throws InterruptedException {
		return this.read(bytes, 0, bytes.length);
	}
	
	/**
	 * Reads {@code len} bytes from the queue into {@code bytes} starting at position {@code pos}. Blocks if there are not enough bytes to read.  
	 * @return the number of bytes actually read (which will always be the same as {@code len} unless the the {@code endWrite()} method has been called).
	 */
	public int read(byte[] bytes, int pos, int len) throws InterruptedException {
		try {
			return read(bytes, pos, len, null);
		} catch (IOException e) {
			// This should never happen (only with OutputStream)
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Reads {@code len} bytes from the queue and writes them to {@code stream}. Blocks if there are not enough bytes to read.  
	 * @return the number of bytes actually read (which will always be the same as {@code len} unless the the {@code endWrite()} method has been called).
	 */
	public int read(OutputStream stream, int len) throws InterruptedException, IOException {
		return read(null, 0, len, stream);
	}

	/**
	 * Skips {@code len} bytes for reading. Blocks if there are not enough bytes to skip. 
	 */
	public int skip(int len) throws InterruptedException {
		try {
			return read(null, 0, len, null);
		} catch (IOException e) {
			// This should never happen (only with OutputStream)
			e.printStackTrace();
			return 0;
		}
	}

	public int peek(byte[] bytes, int pos, int len) throws InterruptedException {
		synchronized (this) {
			while (len > overhead && writing) {
				// Reading faster than writing, block
				this.wait();
			}
			if (len > overhead) {
				// Reaching end of stream (!writing)
				len = overhead;
			}
			if (len > 0) {
				if (readPos + len > capacity()) {
					// Reached the end of the buffer, restart at the beginning
					int split = capacity() - readPos;
					if (bytes != null) {
						System.arraycopy(buffer, readPos, bytes, pos, split);
						System.arraycopy(buffer, 0, bytes, pos + split, len - split);
					} 
					//readPos = len - split;
				} else {
					if (bytes != null) {
						System.arraycopy(buffer, readPos, bytes, pos, len);
					} 
					//readPos += len;
				}
				//overhead -= len;
			} else if (!writing) {
				len = -1;
			} else {
				len = 0;
			}
			return len;
		}
	}
	
	private int read(byte[] bytes, int pos, int len, OutputStream stream) throws InterruptedException, IOException {
		synchronized (this) {
			while (len > overhead && writing) {
				// Reading faster than writing, block
				this.wait();
			}
			if (len > overhead) {
				// Reaching end of stream (!writing)
				len = overhead;
			}
			if (len > 0) {
				if (readPos + len > capacity()) {
					// Reached the end of the buffer, restart at the beginning
					int split = capacity() - readPos;
					if (bytes != null) {
						System.arraycopy(buffer, readPos, bytes, pos, split);
						System.arraycopy(buffer, 0, bytes, pos + split, len - split);
					} else if (stream != null) {
						stream.write(buffer, readPos, split);
						stream.write(buffer, 0, len - split);
					}
					readPos = len - split;
				} else {
					if (bytes != null) {
						System.arraycopy(buffer, readPos, bytes, pos, len);
					} else if (stream != null) {
						stream.write(buffer, readPos, len);
					}
					readPos += len;
				}
				overhead -= len;
			} else if (!writing) {
				len = -1;
			} else {
				len = 0;
			}
			return len;
		}
	}
	
	/**
	 * Resets and clears the queue
	 */
	public void reset() {
		writePos = 0;
		readPos = 0;
		overhead = 0;
		writing = true;
	}
	
	/**
	 * 
	 * @return An InputStream that reads from the queue
	 */
	public InputStream getInputStream() {
		return new InputStream() {

			@Override
			public int read() throws IOException {
				byte[] bb = new byte[1];
				try {
					int len = BlockingByteQueue.this.read(bb);
					if (len == -1)
						return -1;
					else
						return bb[0] & 0xFF;
				} catch (InterruptedException e) {
					e.printStackTrace();
					return -1;
				}			
			}
			
			@Override
			public int read(byte[] bytes, int off, int len) throws IOException {
				try {
					int read = BlockingByteQueue.this.read(bytes, off, len);
					return read;
				} catch (InterruptedException e) {
					e.printStackTrace();
					return -1;
				}
			}
			
		};
	}

	/**
	 * 
	 * @return the number of bytes that are available if the reading should not block
	 */
	public int available() {
		return overhead;
	}
	
	public byte[] toByteArray() {
		byte[] bytes = new byte[available()];
		try {
			read(bytes);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	public byte[] getBuffer() {
		return buffer;
	}


}
