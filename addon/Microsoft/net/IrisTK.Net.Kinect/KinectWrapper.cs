using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
//using System.Runtime.CompilerServices;
using Microsoft.Kinect;

namespace IrisTK.Net.Kinect
{

    public class KinectWrapper {

        public KinectSensor kinectSensor;
        
        private List<SensorElevationListener> elevationListeners = new List<SensorElevationListener>();

        private Queue<string> skeletonQueue = new Queue<string>();

        private List<BodyListener> bodyListeners = new List<BodyListener>();
        private List<DepthFrameListener> depthFrameListeners = new List<DepthFrameListener>();
        private List<ColorFrameListener> colorFrameListeners = new List<ColorFrameListener>();

        private float minDistance = 0.8f;
        private float maxDistance = 2.0f;

        private Timer timer;

        public void start(int index)
        {
            kinectSensor = KinectSensor.KinectSensors[index];
           // Console.WriteLine("Kinect status: " + kinectSensor.Status);
            setNearRangeMode(false);
            setSeatedMode(false);
            kinectSensor.Start();
        }
                
        public void stop()
        {
            kinectSensor.Stop();
        }

        public void addSensorElevationListener(SensorElevationListener listener)
        {
            elevationListeners.Add(listener);
            if (elevationListeners.Count == 1)
                timer = new Timer(CheckElevation, null, 1000, 1000);
        }

        public int getSensorCount()
        {
            return KinectSensor.KinectSensors.Count;
        }

        public int[] mapSkeletonPointToColorPoint(float x, float y, float z)
        {
            SkeletonPoint sp = new SkeletonPoint();
            sp.X = x;
            sp.Y = y;
            sp.Z = z;
            ColorImagePoint cp = kinectSensor.MapSkeletonPointToColor(sp, ColorImageFormat.RawYuvResolution640x480Fps15);
            //ColorImagePoint cp = coordinateMapper.MapSkeletonPointToColorPoint(sp, ColorImageFormat.RgbResolution640x480Fps30);
            return new int[] { cp.X, cp.Y };
        }

        public void setElevationAngle(int angle)
        {
            angle = Math.Max(angle, kinectSensor.MinElevationAngle);
            angle = Math.Min(angle, kinectSensor.MaxElevationAngle);
            kinectSensor.ElevationAngle = angle;
        }

        public void setSeatedMode(bool seated)
        {
            if (seated) {
                kinectSensor.SkeletonStream.TrackingMode = SkeletonTrackingMode.Seated;
            } else {
                kinectSensor.SkeletonStream.TrackingMode = SkeletonTrackingMode.Default;
            }
        }

        public void setNearRangeMode(bool nearRange) {
            if (nearRange)
            {
                    // This will throw on non Kinect For Windows devices.
                    kinectSensor.DepthStream.Range = DepthRange.Near;
                    kinectSensor.SkeletonStream.EnableTrackingInNearRange = true;
                    return;
            }

            kinectSensor.DepthStream.Range = DepthRange.Default;
            kinectSensor.SkeletonStream.EnableTrackingInNearRange = false;
        }

        public void setMinDistance(float value)
        {
            this.minDistance = value;
        }

        public void setMaxDistance(float value)
        {
            this.maxDistance = value;
        }

        public void addBodyListener(BodyListener listener) {
            bodyListeners.Add(listener);
            if (bodyListeners.Count == 1)
            {
                kinectSensor.SkeletonStream.Enable();
                kinectSensor.SkeletonStream.AppChoosesSkeletons = true;
                kinectSensor.SkeletonFrameReady += OnSkeletonFrameReady;
            }
        }

        //[MethodImpl(MethodImplOptions.Synchronized)]
        public void addDepthFrameListener(DepthFrameListener listener)
        {
            depthFrameListeners.Add(listener);
            if (depthFrameListeners.Count == 1)
            {
                kinectSensor.DepthStream.Enable(DepthImageFormat.Resolution320x240Fps30);
                kinectSensor.DepthFrameReady += OnDepthFrameReady;
            }
        }

        private short[] depthPixelData = new short[0];

        //[MethodImpl(MethodImplOptions.Synchronized)]
        private void OnDepthFrameReady(object sender, DepthImageFrameReadyEventArgs eventArgs)
        {
            DepthImageFrame frame = eventArgs.OpenDepthImageFrame();
            if (frame != null)
            {
                try
                {
                    if (depthPixelData.Length != frame.PixelDataLength)
                        depthPixelData = new short[frame.PixelDataLength];
                    frame.CopyPixelDataTo(depthPixelData);
                    foreach (DepthFrameListener listener in depthFrameListeners)
                        listener.onDepthFrameReady(depthPixelData);
                }
                finally
                {
                    if (frame != null) frame.Dispose();
                }
            }
        }

        //[MethodImpl(MethodImplOptions.Synchronized)]
        public void addColorFrameListener(ColorFrameListener listener)
        {
            colorFrameListeners.Add(listener);
            if (colorFrameListeners.Count == 1)
            {
                kinectSensor.ColorStream.Enable(ColorImageFormat.RawYuvResolution640x480Fps15);
                kinectSensor.ColorFrameReady += OnColorFrameReady;
            }
        }

        byte[] colorPixelData = new byte[0];

        //[MethodImpl(MethodImplOptions.Synchronized)]
        private void OnColorFrameReady(object sender, ColorImageFrameReadyEventArgs eventArgs)
        {
            ColorImageFrame frame = eventArgs.OpenColorImageFrame();
            if (frame != null)
            {
                try
                {
                    if (colorPixelData.Length != frame.PixelDataLength)
                        colorPixelData = new byte[frame.PixelDataLength];
                    frame.CopyPixelDataTo(colorPixelData);
                    unsafe
                    {
                        fixed (byte* p = colorPixelData)
                        {
                            foreach (ColorFrameListener listener in new List<ColorFrameListener>(colorFrameListeners))
                                listener.onColorFrameReady((IntPtr)p, frame.Width, frame.Height, ColorFormat.YUV);
                        }
                    }
                }
                finally
                {
                    if (frame != null) frame.Dispose();
                }
            }
        }

        //FaceTrackingViewer.xaml.cs
        private Skeleton[] skeletonData;

        private void OnSkeletonFrameReady(object sender, SkeletonFrameReadyEventArgs eventArgs)
        {
            SkeletonFrame skeletonFrame = null;

            try
            {
                skeletonFrame = eventArgs.OpenSkeletonFrame();

                if (skeletonFrame == null)
                    return;

                // Get the skeleton information
                if (this.skeletonData == null || this.skeletonData.Length != skeletonFrame.SkeletonArrayLength)
                    this.skeletonData = new Skeleton[skeletonFrame.SkeletonArrayLength];

                skeletonFrame.CopySkeletonDataTo(this.skeletonData);

                int min1id = 0;
                int min2id = 0;
                float min1dist = maxDistance;
                float min2dist = maxDistance;
                // Update the list of trackers and the trackers with the current frame information
                foreach (Skeleton skeleton in this.skeletonData)
                {
                    if (skeleton.TrackingState != SkeletonTrackingState.NotTracked)
                    {
                        if (skeleton.Position.Z > minDistance)
                        {
                            if (min2dist <= min1dist && skeleton.Position.Z < min1dist)
                            {
                                min1dist = skeleton.Position.Z;
                                min1id = skeleton.TrackingId;
                            }
                            else if (min1dist <= min2dist && skeleton.Position.Z < min2dist)
                            {
                                min2dist = skeleton.Position.Z;
                                min2id = skeleton.TrackingId;
                            }
                        }
                    }
                }

                if (min1id > 0 && min2id > 0)
                    kinectSensor.SkeletonStream.ChooseSkeletons(min1id, min2id);
                else if (min1id > 0)
                    kinectSensor.SkeletonStream.ChooseSkeletons(min1id);
                else
                    kinectSensor.SkeletonStream.ChooseSkeletons();

                KinectBodySet bodies = new KinectBodySet();

                foreach (Skeleton skeleton in this.skeletonData)
                {
                    if (skeleton.TrackingState == SkeletonTrackingState.Tracked && skeleton.Joints[JointType.Head].TrackingState == JointTrackingState.Tracked)
                    {
                        KinectBody body = new KinectBody();
                        body.Id = (ulong)skeleton.TrackingId;

                        SkeletonPoint head = skeleton.Joints[JointType.Head].Position;
                        body.Head = new KinectBodyPart(head.X, head.Y, head.Z);

                        if (skeleton.Joints[JointType.HandLeft].TrackingState == JointTrackingState.Tracked)
                        {
                            SkeletonPoint hand = skeleton.Joints[JointType.HandLeft].Position;
                            body.HandLeft = new KinectBodyPart(hand.X, hand.Y, hand.Z);
                        }

                        if (skeleton.Joints[JointType.HandRight].TrackingState == JointTrackingState.Tracked)
                        {
                            SkeletonPoint hand = skeleton.Joints[JointType.HandRight].Position;
                            body.HandRight = new KinectBodyPart(hand.X, hand.Y, hand.Z);
                        }

                        bodies.add(body);
                    }
                }

                if (bodies.size() > 0)
                    foreach (BodyListener listener in new List<BodyListener>(bodyListeners))
                        listener.onBodiesReceived(bodies.ToString());

            }
            finally
            {
                if (skeletonFrame != null)
                    skeletonFrame.Dispose();
            }
        }

        private int lastAngle = -1000;

        private void CheckElevation(Object info)
        {
            try
            {
                int angle = kinectSensor.ElevationAngle;
                if (Math.Abs(angle - lastAngle) > 1)
                {
                    foreach (SensorElevationListener listener in new List<SensorElevationListener>(elevationListeners))
                        listener.onSensorElevationChanged(angle);
                    lastAngle = angle;
                }
            }
            catch (InvalidOperationException)
            {
                foreach (SensorElevationListener listener in new List<SensorElevationListener>(elevationListeners))
                    listener.onSensorElevationReadingFailed();
               // Console.WriteLine("Reading Elevation Failed");
            }
        }

        // AUDIO

        private System.IO.Stream audioStream;

        private List<SoundSourceAngleListener> soundSourceAngleListeners = new List<SoundSourceAngleListener>();
        private List<BeamAngleListener> beamAngleListeners = new List<BeamAngleListener>();

        public void addSoundSourceAngleListener(SoundSourceAngleListener listener)
        {
            soundSourceAngleListeners.Add(listener);
            if (soundSourceAngleListeners.Count == 1)
            {
                kinectSensor.AudioSource.SoundSourceAngleChanged += this.SoundSourceAngleChanged;
            }
        }

        private void SoundSourceAngleChanged(Object sender, SoundSourceAngleChangedEventArgs e)
        {
            foreach (SoundSourceAngleListener listener in new List<SoundSourceAngleListener>(soundSourceAngleListeners))
            {
                listener.onSoundSourceAngleChanged((float)e.Angle, (float)e.ConfidenceLevel);
            }
        }

        public void addBeamAngleListener(BeamAngleListener listener)
        {
            beamAngleListeners.Add(listener);
            if (beamAngleListeners.Count == 1)
            {
                kinectSensor.AudioSource.BeamAngleChanged += this.BeamAngleChanged;
            }
        }

        private void BeamAngleChanged(Object sender, BeamAngleChangedEventArgs e)
        {
            foreach (BeamAngleListener listener in new List<BeamAngleListener>(beamAngleListeners))
            {
                listener.onBeamAngleChanged((float)e.Angle);
            }
        }

        public void startAudioStream()
        {
            audioStream = kinectSensor.AudioSource.Start();
        }

        public void stopAudioStream()
        {
            kinectSensor.AudioSource.Stop();
            audioStream = null;
        }

        public byte[] readAudioStream(int count)
        {
            if (audioStream != null)
            {
                var buffer = new byte[count];
                var read = audioStream.Read(buffer, 0, count);

                if (read == count) return buffer;

                if (read == 0) return null;

                var adjustedBuffer = new byte[read];

                for (var i = 0; i < read; i++) adjustedBuffer[i] = buffer[i];

                return adjustedBuffer;
            }
            else
            {
                return null;
            }
        }

    }

}
