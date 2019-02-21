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

        Intent crashIntent = new Intent();

        /*
        * BUG description:
        * Device used: Samsung Galaxy Tab S3

Steps to reproduce:
1. Download my code provided on github (link: )
2. Run my app.
3. Go to any other app that has login form. For instance, Twitter App login can demonstrate this issue.
4. When autofill item will show up, click on it.
5. App might crash here. If not, go back, and do the same thing again. At the 2nd or the 3rd trial the app will crash.

Frequency: 100%

Expected output: Other apps should never crash based on usage of Autofill Framework.

Current output: I get that another app crashed. I don't know the output of another app.

Below, there is an idea what actually happens. The code I have provided demonstrates the use case
        * */

        /*
        * The idea is the following: Autofill Service was able to fill some data, but at the point
        * when user started filling in the data, some circumstances changed, and data can no longer
        * be filled in.
        *
        * Such circumstances might happen pretty often actually. For instance, user opened some app
        * that can be autofilled, then opened another app which autofills other apps, logs out, logs
        * in again. However, the data in another app is already prepared for autofill. But currently,
        * through Autofill Framework, Auth Activity can be initiated in case user would want to autofill.
        *
        * After account has changed, it is a must to ensure data integrity and security. In other words,
        * I can not fill in the data for one account using another account data. This would be massive
        * violation of security.
        *
        * So, the following lines show the point. Let's assume we resolve fields we can fill in.
        * Then, I provide AutofillManager.EXTRA_AUTHENTICATION_RESULT with no data, since another user
        * might actually have no data.
        *
        * In other words, use cases are completely valid and often occuring ones. However, such use cases
        * cause to crash the apps that can be autofilled, and this is a bug I am reporting regarding
        * Autofill Framework behavior. Such use case should be covered without any crashing behavior.
        * */
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
