package com.example.usw.autofillcrashdemo;

import android.app.assist.AssistStructure;
import android.os.CancellationSignal;
import android.service.autofill.AutofillService;
import android.service.autofill.Dataset;
import android.service.autofill.FillCallback;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveCallback;
import android.service.autofill.SaveInfo;
import android.service.autofill.SaveRequest;
import android.support.annotation.NonNull;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyAutofillService extends AutofillService {
    @Override
    public void onFillRequest(
            @NonNull FillRequest request,
            @NonNull CancellationSignal cancellationSignal,
            @NonNull FillCallback callback
    ) {
        AssistStructure structure = request.getFillContexts()
                .get(request.getFillContexts().size() - 1).getStructure();

        List<AutofillId> fillableFields = getFillableFields(structure);

        Dataset.Builder dataSetBuilder = new Dataset.Builder()
                .setAuthentication(AuthActivity.getAuthenticationIntent(this));

        for (AutofillId autofillId : fillableFields) {
            dataSetBuilder.setValue(
                    autofillId,
                    AutofillValue.forText(UUID.randomUUID().toString()),
                    newRemoteViews(UUID.randomUUID().toString())
            );
        }
        Dataset simpleDataSet = dataSetBuilder.build();

        FillResponse.Builder responseBuilder = new FillResponse.Builder();
        responseBuilder.addDataset(simpleDataSet);

        FillResponse response = responseBuilder.build();

        callback.onSuccess(response);
    }

    @Override
    public void onSaveRequest(@NonNull SaveRequest request, @NonNull SaveCallback callback) {
        // Not important here.
    }

    public static List<AutofillId> getFillableFields(AssistStructure structure) {
        int nodeCount = structure.getWindowNodeCount();

        List<AutofillId> autofillIds = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            AssistStructure.ViewNode rootViewNode = structure.getWindowNodeAt(i).getRootViewNode();
            parseViewNodes(autofillIds, rootViewNode);
        }

        return autofillIds;
    }

    private static void parseViewNodes(List<AutofillId> autofillIds, AssistStructure.ViewNode viewNode) {
        if (viewNode.getClassName() != null && viewNode.getClassName().toLowerCase().contains("edittext")) {
            autofillIds.add(viewNode.getAutofillId());
        }

        int childCount = viewNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            parseViewNodes(autofillIds, viewNode.getChildAt(i));
        }
    }

    private RemoteViews newRemoteViews(String remoteViewsText) {
        RemoteViews presentation = new RemoteViews(getPackageName(), R.layout.multidataset_service_list_item);
        presentation.setTextViewText(R.id.text, remoteViewsText);

        return presentation;
    }
}
