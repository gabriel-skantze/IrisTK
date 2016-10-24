using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace IrisTK.Net.Kinect
{
    public class ColorFormat
    {
        public static int YUV = 0;
        public static int BGRA = 1;
    }

    public interface SensorElevationListener
    {
        void onSensorElevationChanged(int elevation);
        void onSensorElevationReadingFailed();
    }

    public interface DepthFrameListener
    {
        void onDepthFrameReady(short[] data);
    }

    public interface ColorFrameListener
    {
        void onColorFrameReady(IntPtr data, int width, int height, int colorFormat);
    }

    public interface BodyListener
    {
        void onBodiesReceived(String data);
    }

    public interface SoundSourceAngleListener
    {
        void onSoundSourceAngleChanged(float angle, float confidence);
    }

    public interface BeamAngleListener
    {
        void onBeamAngleChanged(float angle);
    }

    /*
    public class KinectBodySet
    {
        private List<KinectBody> bodies = new List<KinectBody>();

        public void add(KinectBody Body)
        {
            bodies.Add(Body);
        }

        public KinectBody get(int i)
        {
            return bodies[i];
        }

        public int size()
        {
            return bodies.Count;
        }

    }

    public class KinectBody
    {
        public ulong Id { get; set; }
        public KinectBodyPart Head { get; set; }
        public KinectBodyPart HandLeft { get; set; }
        public KinectBodyPart HandRight { get; set; }

        public bool Happy { get; set; }
        public bool WearingGlasses { get; set; }

        public KinectBody()
        {
            Happy = false;
            WearingGlasses = false;
        }
    }

    public class KinectBodyPart
    {
        public float LocX { get; set; }
        public float LocY { get; set; }
        public float LocZ { get; set; }
        public float RotX { get; set; }
        public float RotY { get; set; }
        public float RotZ { get; set; }
        public bool HasRotation { get; set; }

        public KinectBodyPart(float locX, float locY, float locZ)
        {
            this.LocX = locX;
            this.LocY = locY;
            this.LocZ = locZ;
            this.HasRotation = false;
        }

    }
      */
}
