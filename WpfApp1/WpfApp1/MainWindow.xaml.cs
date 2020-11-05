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
            
            webbrowser.InvokeScript("msg", new object[] { 116.404, 39.92 ,"ok"});
            TcpLoop();
        }

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
            Console.WriteLine(type);
            Console.WriteLine(array2[0]["name"]);

            sendStream.Flush();
            sendStream.Close();
            
        }
    }
}
