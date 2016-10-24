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
package iristk.speech.nuance9;

public class NuanceException extends Exception {
	
	private String problem;
	
	public NuanceException(String cmd, int code) {
		this.problem = "Error when executing " + cmd + ": " + ErrorCode.get(code).name() + "(" + ErrorCode.get(code).code + ")";
	}
	
	public NuanceException(String problem) {
		this.problem = problem;
	}
	
	@Override
	public String toString() {
		return problem;
	}

	public enum ErrorCode {
		SWIrec_SUCCESS(0),
		SWIrec_ERROR_OUT_OF_MEMORY(1),
		SWIrec_ERROR_SYSTEM_ERROR(2), 
		SWIrec_ERROR_UNSUPPORTED(3),
		SWIrec_ERROR_BUSY(4),
		SWIrec_ERROR_INITPROC(5),
		SWIrec_ERROR_INVALID_PARAMETER(6),
		SWIrec_ERROR_INVALID_PARAMETER_VALUE(7),
		SWIrec_ERROR_INVALID_DATA(8),
		SWIrec_ERROR_INVALID_RULE(9),
		SWIrec_ERROR_INVALID_WORD(10),
		SWIrec_ERROR_INVALID_NBEST_INDEX(11),
		SWIrec_ERROR_URI_NOT_FOUND(12),
		SWIrec_ERROR_GRAMMAR_ERROR(13),
		SWIrec_ERROR_AUDIO_OVERFLOW(14),
		SWIrec_ERROR_BUFFER_OVERFLOW(15),
		SWIrec_ERROR_NO_ACTIVE_GRAMMARS(16),
		SWIrec_ERROR_GRAMMAR_NOT_ACTIVATED(17),
		SWIrec_ERROR_NO_SESSION_NAME(18),
		SWIrec_ERROR_INACTIVE(19),
		SWIrec_ERROR_NO_DATA(20),
		SWIrec_ERROR_NO_LICENSE(24),
		SWIrec_ERROR_LICENSE_ALLOCATED(25),
		SWIrec_ERROR_LICENSE_FREED(26),
		SWIrec_ERROR_LICENSE_COMPROMISE(27),
		SWIrec_ERROR_INVALID_MEDIA_TYPE(28),
		SWIrec_ERROR_URI_TIMEOUT(29),
		SWIrec_ERROR_URI_FETCH_ERROR(30),
		SWIrec_ERROR_INVALID_SYSTEM_CONFIGURATION(31),
		SWIrec_ERROR_INVALID_LANGUAGE(32),
		SWIrec_ERROR_DUPLICATE_GRAMMAR(33),
		SWIrec_ERROR_INVALID_CHAR(34);

		public final int code;

		ErrorCode(int code) {
			this.code = code;
		}

		public static ErrorCode get(int code) {
			for (ErrorCode ec : values()) {
				if (ec.code == code) {
					return ec;
				}
			}
			return null;
		}
	}
}
