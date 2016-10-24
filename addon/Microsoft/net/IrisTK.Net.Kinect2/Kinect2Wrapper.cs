using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Kinect;
using Microsoft.Kinect.Face;
using IrisTK.Net.Kinect;

namespace IrisTK.Net.Kinect2
{
    public class Kinect2Wrapper
    {

        private CoordinateMapper coordinateMapper;
        private KinectSensor kinectSensor;

        private BodyFrameReader bodyFrameReader = null;
        private ColorFrameReader colorFrameReader = null;

        private Body[] bodies = null;
        private FrameDescription colorFrameDescription;

        private Queue<string> skeletonQueue = new Queue<string>();

        private List<BodyListener> bodyListeners = new List<BodyListener>();
        private List<ColorFrameListener> colorFrameListeners = new List<ColorFrameListener>();

        private float minDistance = 0.8f;
        private float maxDistance = 2.0f;

        private byte[] colorPixels;
        private long colorPixelsLength;
        private long colorBytesLength;
        private int colorPixelsWidth;
        private int colorPixelsHeight;

        private ushort[] depthPixels;
        private DepthFrameReader depthFrameReader;
        //private DepthSpacePoint[] depthSpacePoints;
        private int depthPixelsWidth;
        private int depthPixelsHeight;

        public void start()
        {
            kinectSensor = KinectSensor.GetDefault();
            coordinateMapper = kinectSensor.CoordinateMapper;

            // faceTracker = new FaceTracker(kinectSensor);
            kinectSensor.Open();

        }

        public void enableDepthMapping()
        {
            if (depthFrameReader == null)
            {
                depthFrameReader = kinectSensor.DepthFrameSource.OpenReader();
                depthFrameReader.FrameArrived += this.depthFrameArrived;
            }
        }

        public void stop()
        {
            kinectSensor.Close();
        }

        public void setMinDistance(float value)
        {
            this.minDistance = value;
        }

        public void setMaxDistance(float value)
        {
            this.maxDistance = value;
        }

        public int[] mapSkeletonPointToColorPoint(float x, float y, float z)
        {
            CameraSpacePoint sp = new CameraSpacePoint();
            sp.X = x;
            sp.Y = y;
            sp.Z = z;
            ColorSpacePoint cp = kinectSensor.CoordinateMapper.MapCameraPointToColorSpace(sp);
            return new int[] { (int)cp.X, (int)cp.Y };
        }

        public float[] mapColorPointToCameraSpace(int x, int y)
        {
            try
            {
                //Console.WriteLine(depthSpacePoints.Length + " " + x + " " + y + " " + colorPixelsWidth);
                //DepthSpacePoint dsp = depthSpacePoints[x + y * colorPixelsWidth];

                int dspX = (x * depthPixelsWidth) / colorPixelsWidth;
                int dspY = (y * depthPixelsHeight) / colorPixelsHeight;

                //Console.WriteLine(depthPixels.Length + " " + dspX + " " + dspY + " " + depthPixelsWidth);
                ushort depth = depthPixels[dspX + dspY * depthPixelsWidth];
                //Console.WriteLine(dspX + " " + dspY + " " + depth);
                if (depth == 0)
                    depth = 1000;
                DepthSpacePoint dsp = new DepthSpacePoint();
                dsp.X = dspX;
                dsp.Y = dspY;
                CameraSpacePoint csp = kinectSensor.CoordinateMapper.MapDepthPointToCameraSpace(dsp, depth);
                return new float[] { csp.X, csp.Y, csp.Z };
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                return null;
            }

        }

        public void addColorFrameListener(ColorFrameListener listener)
        {
            colorFrameListeners.Add(listener);
            if (colorFrameListeners.Count == 1)
            {
                this.colorFrameDescription = this.kinectSensor.ColorFrameSource.CreateFrameDescription(ColorImageFormat.Bgra);
                this.colorPixelsWidth = colorFrameDescription.Width;
                this.colorPixelsHeight = colorFrameDescription.Height;
                this.colorPixelsLength = colorFrameDescription.Width * colorFrameDescription.Height;
                this.colorBytesLength = colorPixelsLength * colorFrameDescription.BytesPerPixel;
                colorPixels = new byte[colorBytesLength];
                colorFrameReader = kinectSensor.ColorFrameSource.OpenReader();
                colorFrameReader.FrameArrived += this.colorFrameArrived;
            }
        }

        public void addBodyListener(BodyListener listener)
        {
            bodyListeners.Add(listener);
            if (bodyListeners.Count == 1)
            {
                bodyFrameReader = kinectSensor.BodyFrameSource.OpenReader();
                bodyFrameReader.FrameArrived += this.bodyFrameArrived;
            }
        }

        private void depthFrameArrived(object sender, DepthFrameArrivedEventArgs e)
        {
            using (DepthFrame depthFrame = e.FrameReference.AcquireFrame())
            {
                if (depthFrame != null)
                {
                    if (depthPixels == null)
                    {
                        depthPixels = new ushort[depthFrame.FrameDescription.LengthInPixels];
                        depthPixelsWidth = depthFrame.FrameDescription.Width;
                        depthPixelsHeight = depthFrame.FrameDescription.Height;
                        //depthSpacePoints = new DepthSpacePoint[colorPixelsLength];
                        //kinectSensor.CoordinateMapper.MapColorFrameToDepthSpace(depthPixels, depthSpacePoints);
                    }
                    lock (depthPixels)
                    {
                        depthFrame.CopyFrameDataToArray(depthPixels);
                    }
                }
            }
        }

        private void colorFrameArrived(object sender, ColorFrameArrivedEventArgs e)
        {
            using (ColorFrame colorFrame = e.FrameReference.AcquireFrame())
            {
                if (colorFrame != null)
                {

                    if (colorFrame.RawColorImageFormat == ColorImageFormat.Bgra)
                    {
                        colorFrame.CopyRawFrameDataToArray(colorPixels);
                    }
                    else
                    {
                        colorFrame.CopyConvertedFrameDataToArray(colorPixels, ColorImageFormat.Bgra);
                    }
                    unsafe
                    {
                        fixed (byte* p = colorPixels)
                        {
                            foreach (ColorFrameListener listener in colorFrameListeners)
                                listener.onColorFrameReady((IntPtr)p, colorFrameDescription.Width, colorFrameDescription.Height, ColorFormat.BGRA);
                        }
                    }
                }
            }
        }

        private Dictionary<ulong, FaceFrameSource> faceFrameSource = new Dictionary<ulong, FaceFrameSource>();
        private Dictionary<ulong, FaceFrameReader> faceFrameReader = new Dictionary<ulong, FaceFrameReader>();
        private Dictionary<ulong, FaceFrameResult> faceFrameResult = new Dictionary<ulong, FaceFrameResult>();
        private Object thisLock = new Object();

        void faceFrameArrived(object sender, FaceFrameArrivedEventArgs e)
        {
            //Console.WriteLine("Frame arrived");
            using (FaceFrame faceFrame = e.FrameReference.AcquireFrame())
            {
                if (faceFrame != null)
                {
                    faceFrameResult[faceFrame.TrackingId] = faceFrame.FaceFrameResult;
                }
            }
        }

        void trackingIdLost(object sender, TrackingIdLostEventArgs e)
        {
            //lock (thisLock) {
            //Console.WriteLine("Tracking lost: " + e.TrackingId);
            faceFrameSource[e.TrackingId] = null;
            faceFrameResult[e.TrackingId] = null;
            //}
        }

        private void bodyFrameArrived(object sender, BodyFrameArrivedEventArgs e)
        {
            lock (thisLock)
            {

                bool dataReceived = false;

                using (BodyFrame bodyFrame = e.FrameReference.AcquireFrame())
                {
                    if (bodyFrame != null)
                    {
                        if (this.bodies == null)
                        {
                            this.bodies = new Body[bodyFrame.BodyCount];
                        }

                        // The first time GetAndRefreshBodyData is called, Kinect will allocate each Body in the array.
                        // As long as those body objects are not disposed and not set to null in the array,
                        // those body objects will be re-used.
                        bodyFrame.GetAndRefreshBodyData(this.bodies);
                        dataReceived = true;
                    }
                }

                if (dataReceived)
                {
                    KinectBodySet bodySet = new KinectBodySet();
                    foreach (Body body in this.bodies)
                    {
                        if (body.IsTracked)
                        {
                            if (!faceFrameSource.ContainsKey(body.TrackingId) || faceFrameSource[body.TrackingId] == null)
                            {
                                faceFrameSource[body.TrackingId] = new FaceFrameSource(KinectSensor.GetDefault(), body.TrackingId, FaceFrameFeatures.RotationOrientation
                    | FaceFrameFeatures.Glasses
                    | FaceFrameFeatures.Happy
                    | FaceFrameFeatures.MouthMoved);

                                faceFrameSource[body.TrackingId].TrackingIdLost += trackingIdLost;
                                faceFrameReader[body.TrackingId] = faceFrameSource[body.TrackingId].OpenReader();
                                faceFrameReader[body.TrackingId].FrameArrived += faceFrameArrived;
                               // Console.WriteLine("Adding tracker for " + body.TrackingId);
                            }

                            IReadOnlyDictionary<JointType, Joint> joints = body.Joints;
                            Joint head = joints[JointType.Head];

                            if (head.TrackingState == TrackingState.Tracked)
                            {
                                KinectBody kbody = new KinectBody();
                                kbody.Id = body.TrackingId;
                                kbody.Head = new KinectBodyPart(head.Position.X, head.Position.Y, head.Position.Z);
                                Joint hand = joints[JointType.HandLeft];
                                if (hand.TrackingState == TrackingState.Tracked)
                                    kbody.HandLeft = new KinectBodyPart(hand.Position.X, hand.Position.Y, hand.Position.Z);
                                hand = joints[JointType.HandRight];
                                if (hand.TrackingState == TrackingState.Tracked)
                                {
                                    kbody.HandRight = new KinectBodyPart(hand.Position.X, hand.Position.Y, hand.Position.Z);
                                }

                                bodySet.add(kbody);

                                if (faceFrameResult.ContainsKey(body.TrackingId) && faceFrameResult[body.TrackingId] != null)
                                {
                                    int pitch, yaw, roll;
                                    FaceFrameResult faceFrame = faceFrameResult[body.TrackingId];
                                    ExtractFaceRotationInDegrees(faceFrame.FaceRotationQuaternion, out pitch, out yaw, out roll);
                                    kbody.Head.HasRotation = true;
                                    kbody.Head.RotX = -pitch;
                                    kbody.Head.RotY = yaw;
                                    kbody.Head.RotZ = roll;
                                    kbody.Happy = faceFrame.FaceProperties[FaceProperty.Happy] == DetectionResult.Yes;
                                    kbody.WearingGlasses = faceFrame.FaceProperties[FaceProperty.WearingGlasses] == DetectionResult.Yes;
                                }
                            }

                        }
                    }

                    if (bodySet.size() > 0)
                    {
                        //if (faceTracker != null)
                        //{
                        //   faceTracker.addFaceData(bodies, bodySet);
                        //}

                        foreach (BodyListener listener in bodyListeners)
                            listener.onBodiesReceived(bodySet.ToString());
                    }
                }
            }
        }


        // AUDIO

        private KinectAudioStream audioStream;

        private List<BeamAngleListener> beamAngleListeners = new List<BeamAngleListener>();

        private float beamAngle = -1000;

        private AudioBeamFrameReader beamFrameReader;

        private float manualBeamAngle = -1000;

        private AudioBeam audioBeam = null;

        public void setManualBeamAngle(float angle)
        {
            manualBeamAngle = angle;
            if (audioBeam != null)
            {
                audioBeam.BeamAngle = manualBeamAngle;
            }
        }

        public void addBeamAngleListener(BeamAngleListener listener)
        {
            beamAngleListeners.Add(listener);
            if (beamAngleListeners.Count == 1)
            {
                beamFrameReader = kinectSensor.AudioSource.OpenReader();
                beamFrameReader.FrameArrived += this.beamFrameArrived;
            }
        }

        public void startAudioStream()
        {
            IReadOnlyList<AudioBeam> audioBeamList = this.kinectSensor.AudioSource.AudioBeams;
            audioBeam = audioBeamList[0];
            if (manualBeamAngle != -1000)
            {
                audioBeam.AudioBeamMode = AudioBeamMode.Manual;
                audioBeam.BeamAngle = manualBeamAngle;
            }
            audioStream = new KinectAudioStream(audioBeam.OpenInputStream());
        }

        public void stopAudioStream()
        {
            audioBeam = null;
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

        private void beamFrameArrived(object sender, AudioBeamFrameArrivedEventArgs e)
        {

            AudioBeamFrameReference frameReference = e.FrameReference;

            AudioBeamFrameList frameList = frameReference.AcquireBeamFrames();

            if (frameList != null)
            {
                // AudioBeamFrameList is IDisposable
                using (frameList)
                {
                    float readAngle = frameList[0].AudioBeam.BeamAngle;
                    if (readAngle != this.beamAngle)
                    {
                        //Console.WriteLine(subFrame.BeamAngle);
                        this.beamAngle = readAngle;
                        foreach (BeamAngleListener listener in beamAngleListeners)
                        {
                            listener.onBeamAngleChanged(beamAngle);
                        }
                    }
                    /*
                    // Only one audio beam is supported. Get the sub frame list for this beam
                    IReadOnlyList<AudioBeamSubFrame> subFrameList = frameList[0].SubFrames;

                    // Loop over all sub frames, extract audio buffer and beam information
                    foreach (AudioBeamSubFrame subFrame in subFrameList)
                    {
                        if (subFrame.BeamAngle != this.beamAngle)
                        {
                            //Console.WriteLine(subFrame.BeamAngle);
                            this.beamAngle = subFrame.BeamAngle;
                            foreach (BeamAngleListener listener in beamAngleListeners)
                            {
                                listener.onBeamAngleChanged(subFrame.BeamAngle);
                            }
                        }
                    }
                     * */
                }
            }

        }

        private static void ExtractFaceRotationInDegrees(Vector4 rotQuaternion, out int pitch, out int yaw, out int roll)
        {
            double x = rotQuaternion.X;
            double y = rotQuaternion.Y;
            double z = rotQuaternion.Z;
            double w = rotQuaternion.W;

            // convert face rotation quaternion to Euler angles in degrees
            double yawD, pitchD, rollD;
            pitchD = Math.Atan2(2 * ((y * z) + (w * x)), (w * w) - (x * x) - (y * y) + (z * z)) / Math.PI * 180.0;
            yawD = Math.Asin(2 * ((w * y) - (x * z))) / Math.PI * 180.0;
            rollD = Math.Atan2(2 * ((x * y) + (w * z)), (w * w) + (x * x) - (y * y) - (z * z)) / Math.PI * 180.0;

            // clamp the values to a multiple of the specified increment to control the refresh rate
            double increment = 1;
            pitch = (int)((pitchD + ((increment / 2.0) * (pitchD > 0 ? 1.0 : -1.0))) / increment) * (int)increment;
            yaw = (int)((yawD + ((increment / 2.0) * (yawD > 0 ? 1.0 : -1.0))) / increment) * (int)increment;
            roll = (int)((rollD + ((increment / 2.0) * (rollD > 0 ? 1.0 : -1.0))) / increment) * (int)increment;
        }

    }

}
