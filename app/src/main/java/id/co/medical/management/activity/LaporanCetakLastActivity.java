package id.co.medical.management.activity;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.medical.management.R;

public class LaporanCetakLastActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.web_view_PDF) WebView pdfViewer;

    WebSettings webSettings;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_cetak_last);
        ButterKnife.bind(LaporanCetakLastActivity.this);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(R.string.title_activity_laporan_cetak_last);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        webSettings = pdfViewer.getSettings();
        webSettings.setJavaScriptEnabled(true); // Untuk mengaktifkan javascript
        webSettings.getUseWideViewPort(); // Untuk support mediaquery

        url = getIntent().getStringExtra("keyData");

        pdfViewer.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

            }
        });
        pdfViewer.setWebViewClient(new WebViewClient(){
            // Ketika webview error atau selesai load page loading akan dismiss
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }
            @Override
            public void onPageFinished(WebView view, String url) {

            }
        });
        pdfViewer.loadUrl("https://docs.google.com/viewerng/viewer?url="+url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fragment_olahraga, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_about){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LaporanCetakLastActivity.this);
            alertDialogBuilder.setTitle("Tentang Aplikasi");
            alertDialogBuilder
                    .setMessage("Diabetes Manager\n" +
                            "Applications version "+getString(R.string.version))
                    .setCancelable(false)
                    .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
