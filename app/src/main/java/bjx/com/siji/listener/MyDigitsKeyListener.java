package bjx.com.siji.listener;

import android.content.Context;
import android.text.InputType;
import android.text.method.DigitsKeyListener;

import bjx.com.siji.R;


/**
 * Created by jqc on 2017/10/20.
 * 字符数字限制a-z,0-9组成
 */

public class MyDigitsKeyListener extends DigitsKeyListener {

    Context mContext;

    public MyDigitsKeyListener(Context context) {
        mContext = context;
    }

    @Override
    public int getInputType() {
        return InputType.TYPE_TEXT_VARIATION_PASSWORD;
    }

    @Override
    protected char[] getAcceptedChars() {
        char[] data = mContext.getResources().getString(R.string.lettersnum_input).toCharArray();
        return data;
    }

}
