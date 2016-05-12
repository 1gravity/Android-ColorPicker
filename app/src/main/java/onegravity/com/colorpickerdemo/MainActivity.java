package onegravity.com.colorpickerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.onegravity.colorpicker.ColorPickerDialog;
import com.onegravity.colorpicker.OnColorChangedListener;
import com.onegravity.colorpicker.SetColorChangedListenerEvent;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends AppCompatActivity {

    private View mRootLayout;

    private int mDialogId = -1;

    private int mColor = 0x88000088;

    private OnColorChangedListener mListener = new OnColorChangedListener() {
        @Override
        public void onColorChanged(int color, boolean dialogClosing) {
            mColor = color;
            mRootLayout.setBackgroundColor(mColor);
            if (dialogClosing) {
                mDialogId = -1;
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mDialogId = savedInstanceState.getInt("mDialogId");
            mColor = savedInstanceState.getInt("mColor");
        }

        setContentView(R.layout.main);
        mRootLayout = findViewById(R.id.root_layout);
        mRootLayout.setBackgroundColor(mColor);

        if (mDialogId == -1) {
            // the dialog will stay open so don't open it again after an orientation change
            ColorPickerDialog dialog = new ColorPickerDialog(this, mColor, true).show();
            mDialogId = dialog.getId();
        }

        EventBus.getDefault().post(new SetColorChangedListenerEvent(mDialogId, mListener));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("mDialogId", mDialogId);
        outState.putInt("mColor", mColor);
    }

}
