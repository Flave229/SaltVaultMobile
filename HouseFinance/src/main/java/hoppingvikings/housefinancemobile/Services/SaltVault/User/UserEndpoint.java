package hoppingvikings.housefinancemobile.Services.SaltVault.User;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import hoppingvikings.housefinancemobile.ApiErrorCodes;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.Person;
import hoppingvikings.housefinancemobile.WebService.CommunicationRequest;
import hoppingvikings.housefinancemobile.WebService.CommunicationResponse;
import hoppingvikings.housefinancemobile.WebService.HTTPHandler;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;

public class UserEndpoint extends HTTPHandler
{
    private final String USER_ENDPOINT = "http://house.flave.co.uk/api/v2/Users";
    private final SessionPersister _session;

    @Inject
    public UserEndpoint(SessionPersister session)
    {
        _session = session;
    }

    @Override
    protected CommunicationRequest ConstructGet(String urlAdditions)
    {
        SetRequestProperty("Authorization", _session.GetSessionID());
        return new CommunicationRequest()
        {{
            ItemTypeData = ItemType.PERSON;
            Endpoint = USER_ENDPOINT;
            OwnerV2 = UserEndpoint.this;
        }};
    }

    @Override
    protected CommunicationRequest ConstructPost(JSONObject postData) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
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
                ApiErrorCodes errorCode = ApiErrorCodes.get(result.Response.getJSONObject("error").getInt("errorCode"));
                if(errorCode == ApiErrorCodes.SESSION_EXPIRED || errorCode == ApiErrorCodes.SESSION_INVALID) {
                    result.Callback.OnFail(result.RequestTypeData, String.valueOf(errorCode.getValue()));
                }
                else
                {
                    String errorMessage = result.Response.getJSONObject("error").getString("message");
                    result.Callback.OnFail(result.RequestTypeData, errorMessage);
                }
                return;
            }

            if (result.RequestTypeData == RequestType.GET)
            {
                HandlePersonListResponse(result);
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

    private void HandlePersonListResponse(CommunicationResponse result)
    {
        JSONArray returnedObject;
        ArrayList<JSONObject> userObjects = new ArrayList<>();
        ArrayList<Person> parsedUsers = new ArrayList<>();

        try
        {
            returnedObject = result.Response.getJSONArray("people");

            for(int i = 0; i < returnedObject.length(); i++)
            {
                userObjects.add(returnedObject.getJSONObject(i));
            }

            for (int j = 0; j < userObjects.size(); j++)
            {
                parsedUsers.add(new Person(userObjects.get(j)));
            }

            result.Callback.OnSuccess(result.RequestTypeData, parsedUsers);
        }
        catch (JSONException je)
        {
            je.printStackTrace();
            result.Callback.OnFail(result.RequestTypeData, "Could not obtain People");
        }
        catch(Exception e)
        {
            result.Callback.OnFail(result.RequestTypeData, "Could not obtain People");
        }
    }
}
