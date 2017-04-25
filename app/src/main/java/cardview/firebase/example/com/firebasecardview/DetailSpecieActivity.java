package cardview.firebase.example.com.firebasecardview;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DetailSpecieActivity extends AppCompatActivity {

    TextView titleTxt, descriptionTxt;
    ImageView specieImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_specie);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titleTxt = (TextView) findViewById(R.id.titleDetailTxt);
        descriptionTxt = (TextView) findViewById(R.id.descriptionDetailTxt);
        specieImage = (ImageView) findViewById(R.id.specieDetailImg);

        Intent i = this.getIntent();

        String title = i.getExtras().getString("TITLE_KEY");
        String description = i.getExtras().getString("DESC_KEY");
        String image = i.getExtras().getString("IMG_KEY");


    }
}
