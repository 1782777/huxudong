using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.IO;


using System.Net.Sockets;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Resources;
using System.Net;

namespace WpfApp1
{
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            webbrowser.InvokeScript("clean");
            
            TcpLoop();
        }

        List<Team> TEAMLIST = new List<Team>();
        private void TcpLoop() {
            TcpClient client = new TcpClient("182.92.114.73", 8899);
            NetworkStream sendStream = client.GetStream();
            string jsonText = @"{""type"" : ""getteams""}";
            Byte[] sendBytes = Encoding.Default.GetBytes(jsonText);
            sendStream.Write(sendBytes, 0, sendBytes.Length);

            NetworkStream ns = client.GetStream();
            byte[] bytes = new byte[2048];
            int bytesread = ns.Read(bytes, 0, bytes.Length);
            string msg = Encoding.Default.GetString(bytes, 0, bytesread);
            Console.WriteLine(msg);
            JObject studentsJson = JObject.Parse(msg);

            string type = studentsJson["type"].ToString();

            JArray array2 = JArray.Parse(studentsJson["plist"].ToString());
            
            for (int i = 0; i < array2.Count; i++) {
                Console.WriteLine(array2[i]["name"] +":"+ array2[i]["lon"].ToString() + ":" + array2[i]["lat"].ToString() + ":" + array2[i]["score"].ToString());
                
            }
            foreach (JObject j in array2)
            {
                bool has = false;
                foreach (Team t in TEAMLIST) {
                    if (t.teamName.Equals(j["name"].ToString()))
                    {
                        has = true;
                        //update
                        double[] res = getBaiducoor(new double[] { double.Parse(j["lon"].ToString()), double.Parse(j["lat"].ToString())});
                        t.lon = res[0];
                        t.lat = res[1];
                        Console.WriteLine(t.lon +":"+t.lat);
                        t.score = int.Parse(j["score"].ToString());
                        continue;
                    }
                }
                if (!has)
                {
                    Team team = new Team(j["name"].ToString());
                    TEAMLIST.Add(team);
                }

            }

            foreach (Team t in TEAMLIST) {
                Console.WriteLine((float)t.lon);
                
                webbrowser.InvokeScript("update_make",new object[] { t.lon,t.lat, t.score.ToString(),t.teamName });

            }


            sendStream.Flush();
            sendStream.Close();
            
        }

        public static double[] getBaiducoor(double[] coord)//坐标转换的方法
        {
            double longitude = coord[0];
            double latitude = coord[1];
            //需要转的gps经纬度
            string convertUrl = "http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=" + longitude + "&y=" + latitude + "";

            HttpWebRequest request = (HttpWebRequest)System.Net.HttpWebRequest.Create(convertUrl);//创建http请求
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            Stream responseStream = response.GetResponseStream();
            StreamReader sr = new StreamReader(responseStream, Encoding.GetEncoding("utf-8"));
            string responseTxt = sr.ReadToEnd();
            sr.Close();
            sr.Dispose();

            CoorConvert mapconvert = new CoorConvert();//创建存放结果的对象
            mapconvert = JsonConvert.DeserializeObject<CoorConvert>(responseTxt);//赋值

            string lon = mapconvert.x;
            string lat = mapconvert.y;

            byte[] xBuffer = Convert.FromBase64String(lon);//坐标base64解密
            string strX = Encoding.UTF8.GetString(xBuffer, 0, xBuffer.Length);

            byte[] yBuffer = Convert.FromBase64String(lat);
            string strY = Encoding.UTF8.GetString(yBuffer, 0, xBuffer.Length);

            double[] coor = new double[2];
            coor[0] = Convert.ToDouble(strX);
            coor[1] = Convert.ToDouble(strY);
            return coor;
        }

        public class CoorConvert
        {
            public string error { get; set; }
            public string x { get; set; }
            public string y { get; set; }
        }

    }

    public class Team {

        public string teamName;
        public int score;
        public double lon, lat;
        public Team(string name)
        {
            Console.WriteLine("init a team" + name);
            this.teamName = name;
            this.score = 0;
            this.lon = 0.0;
            this.lat = 0.0;
        }
    }
}
