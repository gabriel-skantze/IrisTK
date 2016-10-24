using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IrisTK.Net.Kinect
{
    public class KinectBodySet
    {
        private List<KinectBody> bodies = new List<KinectBody>();

        public void add(KinectBody Body)
        {
            bodies.Add(Body);
        }

        public int size()
        {
            return bodies.Count;
        }

        public override String ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append("{\"bodies\":[");
            int i = 0;
            foreach (KinectBody kb in bodies)
            {
                if (i > 0) sb.Append(",");
                sb.Append(kb.ToString());
                i++;
            }
            sb.Append("]}");
            return sb.ToString();
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

        public override String ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append("{\"class\":\"iristk.situated.Body\",\"id\":\"" + Id + "\"");
            if (Head != null)
                sb.Append(",\"head\":" + Head.ToString());
            if (HandLeft != null)
                sb.Append(",\"handLeft\":" + HandLeft.ToString());
            if (HandRight != null)
                sb.Append(",\"handRight\":" + HandRight.ToString());
            sb.Append("}");
            return sb.ToString();
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

        public override String ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append("{\"class\":\"iristk.situated.BodyPart\",");
            sb.Append("\"location\":{\"class\":\"iristk.situated.Location\",\"x\":" + Math.Round(LocX, 3) + ",\"y\":" + Math.Round(LocY, 3) + ",\"z\":" + Math.Round(LocZ, 3) + "}");
            if (HasRotation)
                sb.Append(",\"rotation\":{\"class\":\"iristk.situated.Rotation\",\"x\":" + Math.Round(RotX, 3) + ",\"y\":" + Math.Round(RotY, 3) + ",\"z\":" + Math.Round(RotZ, 3) + "}");
            sb.Append("}");
            return sb.ToString();
        }

    }
}
