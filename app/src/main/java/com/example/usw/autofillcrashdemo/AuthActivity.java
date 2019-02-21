package com.example.usw.autofillcrashdemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillManager;

import java.util.List;

public class AuthActivity extends AppCompatActivity {

    @Override public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // This behavior simulating no longer existing dataset causes other apps to crash.
        Intent crashIntent = new Intent();

        List<AutofillId> fillableFields = MyAutofillService.getFillableFields(getIntent().<AssistStructure>getParcelableExtra(AutofillManager.EXTRA_ASSIST_STRUCTURE));
        AutofillId[] required = fillableFields.toArray(new AutofillId[fillableFields.size() - 1]);

        crashIntent.putExtra(AutofillManager.EXTRA_AUTHENTICATION_RESULT, new FillResponse.Builder().setSaveInfo(new SaveInfo.Builder(SaveInfo.SAVE_DATA_TYPE_GENERIC, required).build()).build());
        setResult(Activity.RESULT_OK, crashIntent);
    }

    public static IntentSender getAuthenticationIntent(Context context) {
        Intent intent = new Intent(context, AuthActivity.class);

        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT).getIntentSender();
    }

}
