
package ir.ictman.playstoredl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipboardManager;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getLink= findViewById(R.id.btn_get_link);
        Button btnPaste = findViewById(R.id.btn_paste);
        EditText etInputUrl = findViewById(R.id.tv_input_url);
        TextView tvDownloadUrl = findViewById(R.id.tv_download_url);
        TextView tvDownloadSize = findViewById(R.id.tv_download_size);
        Button btnCopy = findViewById(R.id.btn_copy);
        Button btnShare = findViewById(R.id.btn_share);
        Button btnClear = findViewById(R.id.btn_clear);
        Button btnDownload = findViewById(R.id.btn_download);


        class JsoupParseTask extends AsyncTask<String, Void, Document> {
            @Override
            protected Document doInBackground(String... urls) {

                String getUrl = urls[0];

                Document document = null;
                try {
                    document = Jsoup.connect("https://apk.support/app/" + getUrl + "#latest_version").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return document;
            }

            @Override
            protected void onPostExecute(Document doc) {
                if (doc==null){
                    //tvDownloadUrl.setText("APK not exist.\nURL or package Name is wrong");
                    //tvDownloadUrl.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_red));
                    Context cnt = getApplicationContext();
                    Toast.makeText(cnt, "APK not exist.\nURL or package Name is wrong", Toast.LENGTH_LONG).show();
                }
                else {
                    Elements links = doc.select("#pageapp > div.browser_a > div > ul > li:nth-child(1) > a");
                    String linkHref = links.attr("href");
                    tvDownloadUrl.setText(linkHref);
                    Elements size = doc.select("#pageapp > div.browser_a > div > ul > li:nth-child(1) > a > span");
                    String appSize = size.html();
                    tvDownloadSize.setText("Apk info: " + appSize);
                }

            }

        }
        getLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlStr = "";
                String inputUrl = etInputUrl.getText().toString();
                if (inputUrl.isEmpty()) {
                    Context cnt = getApplicationContext();
                    Toast.makeText(cnt, "Enter URL or Package Name", Toast.LENGTH_LONG).show();
                    return;
                }
                if (inputUrl.startsWith("https://")) {
                    urlStr = inputUrl.substring(46);
                }
                else {
                    urlStr = inputUrl;
                }
                if (urlStr.contains("&hl=")) {
                    int chIndex = urlStr.indexOf("&");
                    urlStr = urlStr.substring(0,chIndex -1);
                    int z =0;
                }
                new JsoupParseTask().execute(urlStr);

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etInputUrl.setText("");
                tvDownloadUrl.setText("");
                tvDownloadSize.setText("");
                etInputUrl.requestFocus();
                Context cnt = getApplicationContext();
                Toast.makeText(cnt, "Clear Done", Toast.LENGTH_LONG).show();
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("APK Download Link", tvDownloadUrl.getText().toString());
                clipboard.setPrimaryClip(clip);
                Context cnt = getApplicationContext();
                Toast.makeText(cnt, "Copy Done", Toast.LENGTH_SHORT).show();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = tvDownloadUrl.getText().toString();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "URL:");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, s);
                startActivity(Intent.createChooser(sharingIntent, "Share text via"));
            }
        });

        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String pasteData = "";
                if(clipboard.hasPrimaryClip()){
                    ClipData pData = clipboard.getPrimaryClip();
                    ClipData.Item item = pData.getItemAt(0);
                    etInputUrl.setText(item.getText().toString());
                    Context cnt = getApplicationContext();
                    Toast.makeText(cnt, "Paste Done", Toast.LENGTH_SHORT).show();
                }
                else {
                    Context cnt = getApplicationContext();
                    Toast.makeText(cnt, "Nothing exist!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileUrl = tvDownloadUrl.getText().toString();
                if (fileUrl.isEmpty()) {
                    Context cnt = getApplicationContext();
                    Toast.makeText(cnt, "Nothing exist for download!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(fileUrl));
                    startActivity(browserIntent);
                }

            }
        });
    }
}