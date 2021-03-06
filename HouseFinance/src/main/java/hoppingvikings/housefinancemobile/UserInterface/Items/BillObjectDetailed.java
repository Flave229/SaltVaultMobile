package hoppingvikings.housefinancemobile.UserInterface.Items;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BillObjectDetailed {
    public int id = 0;
    public String name = "";
    public String dateDue = "";
    public double amountDue = 0.0;
    public double amountTotal = 0.0;
    public double amountPaid = 0.0;
    public ArrayList<BillPayment> paymentDetails = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public BillObjectDetailed(JSONObject details, JSONArray payments)
    {
        try {
            id = details.getInt("id");
            name = details.getString("name");
            dateDue = dateFormat.format(dateFormat.parse(details.getString("fullDateDue")));
            amountPaid = details.getDouble("amountPaid");
            amountTotal = details.getDouble("totalAmount");
            amountDue = (amountTotal - amountPaid);
            if(payments.length() > 0)
            {
                for(int i = 0; i < payments.length(); i++)
                {
                    paymentDetails.add(new BillPayment(payments.getJSONObject(i), id));
                }
            }
        } catch (JSONException e)
        {

        }
        catch (ParseException e)
        {
            Log.i("Info: ", "Failed to parse date " + e.getMessage());
        }
    }
}
