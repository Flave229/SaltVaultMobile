package hoppingvikings.housefinancemobile.Endpoints.SaltVault;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.WebService.CommunicationRequest;
import hoppingvikings.housefinancemobile.WebService.CommunicationResponse;
import hoppingvikings.housefinancemobile.WebService.HTTPHandler;
import hoppingvikings.housefinancemobile.WebService.RequestType;

public class HouseInviteEndpoint extends HTTPHandler
{
    private final String HOUSEHOLD_INVITE_ENDPOINT = "http://house.flave.co.uk/api/v2/Household/InviteLink";

    @Override
    protected CommunicationRequest ConstructGet(String urlAdditions)
    {
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.HOUSEHOLD_INVITE;
            Endpoint = HOUSEHOLD_INVITE_ENDPOINT;
            OwnerV2 = HouseInviteEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPost(final JSONObject postData)
    {
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.HOUSEHOLD_INVITE;
            Endpoint = HOUSEHOLD_INVITE_ENDPOINT;
            RequestBody = String.valueOf(postData);
            OwnerV2 = HouseInviteEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPatch(JSONObject patchData) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    protected CommunicationRequest ConstructDelete(JSONObject deleteData) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void HandleResponse(CommunicationResponse result)
    {
        try
        {
            if(result.Response.has("hasError") && result.Response.getBoolean("hasError"))
            {
                String errorMessage = result.Response.getJSONObject("error").getString("message");
                Log.e("Error", errorMessage);
                result.Callback.OnFail(result.RequestTypeData, errorMessage);
                return;
            }

            if (result.RequestTypeData == RequestType.GET)
            {
                HandleHouseholdInviteResponse(result);
            }
            else
                result.Callback.OnSuccess(result.RequestTypeData, null);
        }
        catch (JSONException je)
        {
            je.printStackTrace();
            result.Callback.OnFail(result.RequestTypeData, "Failed to parse the response from the server");
        }
        catch(Exception e)
        {
            result.Callback.OnFail(result.RequestTypeData, "Failed to handle the response from the server");
        }
    }

    private void HandleHouseholdInviteResponse(CommunicationResponse result)
    {
        try
        {
            if(result.Response.has("inviteLink"))
            {
                result.Callback.OnSuccess(result.RequestTypeData, result.Response.getString("inviteLink"));
                return;
            }

            result.Callback.OnFail(result.RequestTypeData, "Failed to obtain invite link");
        }
        catch (JSONException je)
        {
            result.Callback.OnFail(result.RequestTypeData, "Failed to obtain invite link");
        }
        catch (Exception e)
        {
            result.Callback.OnFail(result.RequestTypeData, "Failed to obtain invite link");
        }
    }
}