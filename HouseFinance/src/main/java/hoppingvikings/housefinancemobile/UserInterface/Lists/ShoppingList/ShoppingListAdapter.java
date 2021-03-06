package hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingList;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.BitmapCache;
import hoppingvikings.housefinancemobile.Notifications.NotificationType;
import hoppingvikings.housefinancemobile.NotificationWrapper;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.Services.SaltVault.Shopping.ShoppingEndpoint;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListObject;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.CardViewHolder>
            implements CommunicationCallback
{
    private static ShoppingItemClickedListener _listener;
    private final NotificationWrapper _notificationWrapper;
    private final ShoppingEndpoint _shoppingEndpoint;

    public interface ShoppingItemClickedListener
    {
        void onShoppingItemClick(View itemView, int pos);
    }

    public void setOnShoppingItemClickListener(ShoppingItemClickedListener listener)
    {
        _listener = listener;
    }

    public interface DeleteCallback
    {
        void onItemDeleted();
    }

    private static DeleteCallback _deletecallback;
    private static EditPressedCallback _editCallback;

    public interface EditPressedCallback
    {
        void onEditPressed(int itemid);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        View view;
        LinearLayout cardView;
        TextView shoppingItemName;
        TextView addedByText;
        TextView addedDate;
        TextView addedForText;
        TextView infoText;
        ImageView addedBy1;
        ImageView addedFor1;
        ImageView addedFor2;
        ImageView addedFor3;

        LinearLayout buttonsContainer;
        ImageButton completeButton;
        ImageButton editButton;
        ImageButton notifyButton;
        ImageButton deleteButton;

        public CardViewHolder(View v) {
            super(v);
            view = v;
            cardView = (LinearLayout) v.findViewById(R.id.shoppingItemCard);
            shoppingItemName = (TextView) v.findViewById(R.id.shoppingItemName);
            addedDate = (TextView) v.findViewById(R.id.addedDate);
            addedForText = (TextView)v.findViewById(R.id.addedForText);
            infoText = (TextView)v.findViewById(R.id.info_text);
            addedByText = (TextView)v.findViewById(R.id.addedByText);
            addedBy1 = (ImageView) v.findViewById(R.id.addedBy);
            addedFor1 = (ImageView) v.findViewById(R.id.addedFor1);
            addedFor2 = (ImageView) v.findViewById(R.id.addedFor2);
            addedFor3 = (ImageView) v.findViewById(R.id.addedFor3);

            buttonsContainer = (LinearLayout) v.findViewById(R.id.buttonsContainer);
            completeButton = (ImageButton) v.findViewById(R.id.shopping_complete);
            editButton = (ImageButton) v.findViewById(R.id.shopping_edit);
            notifyButton = (ImageButton) v.findViewById(R.id.shopping_notify);
            deleteButton = (ImageButton) v.findViewById(R.id.shopping_delete);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(_listener != null)
                    {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION)
                        {
                            _listener.onShoppingItemClick(view, pos);
                        }
                    }
                }
            });
        }
    }

    ArrayList<ShoppingListObject> _shoppingItems = new ArrayList<>();
    private Context _context;
    BitmapCache imgCache;
    private int selected;
    private boolean completeAlreadyPressed = false;

    public void SetDeleteCallback(DeleteCallback owner)
    {
        _deletecallback = owner;
    }
    public void SetEditPressedCallback(EditPressedCallback owner)
    {_editCallback = owner;}

    public ShoppingListAdapter(Context context, NotificationWrapper notificationWrapper, ShoppingEndpoint shoppingEndpoint, ArrayList<ShoppingListObject> items)
    {
        _context = context;
        _notificationWrapper = notificationWrapper;
        _shoppingEndpoint = shoppingEndpoint;
        _shoppingItems.addAll(items);
        long maxMem = (Runtime.getRuntime().maxMemory() / 1024 / 1024);
        imgCache = new BitmapCache((maxMem / 4L) * 1024L * 1024L, _context);
    }

    @Override
    public int getItemCount()
    {
        if (_shoppingItems != null)
            return _shoppingItems.size();
        else
            return 0;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_shopping, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder cvh, final int i)
    {
        cvh.shoppingItemName.setText(_shoppingItems.get(cvh.getAdapterPosition()).ItemName);
        cvh.addedDate.setText(_shoppingItems.get(cvh.getAdapterPosition()).AddedDate);

        if(_shoppingItems.get(cvh.getAdapterPosition()).Purchased)
        {
            cvh.cardView.setBackgroundResource(R.color.bill_paid);

            cvh.addedByText.setTextColor(_context.getResources().getColor(R.color.bill_paid_text));
            cvh.addedDate.setTextColor(_context.getResources().getColor(R.color.bill_paid_text));
            cvh.addedForText.setTextColor(_context.getResources().getColor(R.color.bill_paid_text));
            cvh.shoppingItemName.setTextColor(_context.getResources().getColor(R.color.bill_paid_text));
            cvh.infoText.setTextColor(_context.getResources().getColor(R.color.bill_paid_text));
        }
        else
        {
            cvh.cardView.setBackgroundColor(Color.WHITE);

            cvh.addedByText.setTextColor(Color.BLACK);
            cvh.addedDate.setTextColor(Color.BLACK);
            cvh.addedForText.setTextColor(Color.BLACK);
            cvh.shoppingItemName.setTextColor(Color.BLACK);
            cvh.infoText.setTextColor(Color.BLACK);
        }

        try
        {
            imgCache.PutBitmap(_shoppingItems.get(cvh.getAdapterPosition()).AddedFor.get(0).ImageUrl, cvh.addedFor1);

            if (_shoppingItems.get(cvh.getAdapterPosition()).AddedFor.size() < 2)
            {
                cvh.addedFor2.setVisibility(View.INVISIBLE);
            }
            else
            {
                cvh.addedFor2.setVisibility(View.VISIBLE);
                imgCache.PutBitmap(_shoppingItems.get(cvh.getAdapterPosition()).AddedFor.get(1).ImageUrl, cvh.addedFor2);
            }

            if (_shoppingItems.get(cvh.getAdapterPosition()).AddedFor.size() < 3)
            {
                cvh.addedFor3.setVisibility(View.INVISIBLE);
            }
            else
            {
                cvh.addedFor3.setVisibility(View.VISIBLE);
                imgCache.PutBitmap(_shoppingItems.get(cvh.getAdapterPosition()).AddedFor.get(2).ImageUrl, cvh.addedFor3);
            }

            if(_shoppingItems.get(cvh.getAdapterPosition()).ItemExpanded)
            {
                cvh.editButton.setVisibility(View.VISIBLE);
                cvh.buttonsContainer.setVisibility(View.VISIBLE);
                cvh.addedByText.setVisibility(View.VISIBLE);
                cvh.infoText.setVisibility(View.INVISIBLE);
                cvh.addedBy1.setVisibility(View.VISIBLE);

                imgCache.PutBitmap(_shoppingItems.get(cvh.getAdapterPosition()).AddedBy.ImageUrl, cvh.addedBy1);

                cvh.editButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        _editCallback.onEditPressed(_shoppingItems.get(cvh.getAdapterPosition()).Id);
                    }
                });

                cvh.deleteButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        try
                        {
                            JSONObject itemJson = new JSONObject();
                            itemJson.put("ShoppingItemId", _shoppingItems.get(cvh.getAdapterPosition()).Id);
                            _shoppingEndpoint.Delete(_context, ShoppingListAdapter.this, itemJson);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(_context, "Failed to delete item", Toast.LENGTH_SHORT).show();
                        }
                        NotificationManager man = (NotificationManager) _context.getSystemService(NOTIFICATION_SERVICE);
                        man.cancel(_shoppingItems.get(cvh.getAdapterPosition()).Id);
                        _shoppingItems.remove(cvh.getAdapterPosition());
                        notifyItemRemoved(cvh.getAdapterPosition());
                        notifyItemRangeChanged(cvh.getAdapterPosition(), _shoppingItems.size());
                    }
                });

                cvh.completeButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(!completeAlreadyPressed)
                        {
                            completeAlreadyPressed = true;
                            selected = cvh.getAdapterPosition();
                            JSONObject editedItem = new JSONObject();
                            try
                            {
                                editedItem.put("Id", _shoppingItems.get(cvh.getAdapterPosition()).Id);
                                editedItem.put("Purchased", !_shoppingItems.get(cvh.getAdapterPosition()).Purchased);
                                _shoppingEndpoint.Patch(_context, ShoppingListAdapter.this, editedItem);
                                // TODO: Ask Josh what this Notification thing is. Should it be in the NotificationWrapper?
                                NotificationManager man = (NotificationManager) _context.getSystemService(NOTIFICATION_SERVICE);
                                man.cancel(_shoppingItems.get(cvh.getAdapterPosition()).Id);
                            }
                            catch (Exception e)
                            {
                                OnFail(RequestType.PATCH, "");
                            }
                        }
                    }
                });

                if(_shoppingItems.get(cvh.getAdapterPosition()).Purchased)
                {
                    cvh.notifyButton.setVisibility(View.INVISIBLE);
                    cvh.completeButton.setImageResource(R.drawable.ic_undo_black_24dp);
                    cvh.editButton.setVisibility(View.GONE);
                }
                else
                    {
                    cvh.notifyButton.setVisibility(View.VISIBLE);
                    cvh.completeButton.setVisibility(View.VISIBLE);
                    cvh.completeButton.setImageResource(R.drawable.ic_done_black_24dp);
                    cvh.editButton.setVisibility(View.VISIBLE);
                    cvh.notifyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _notificationWrapper.ShowNotification(NotificationType.SHOPPING,_shoppingItems.get(cvh.getAdapterPosition()).ItemName, "Reminder", _shoppingItems.get(cvh.getAdapterPosition()).Id);
                        }
                    });
                }
            }
            else
            {
                cvh.buttonsContainer.setVisibility(View.GONE);
                cvh.infoText.setVisibility(View.VISIBLE);
                cvh.addedByText.setVisibility(View.INVISIBLE);
                cvh.addedBy1.setVisibility(View.GONE);
            }

        } catch (Exception e)
        {
            Log.v("Error: ", e.getMessage());
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView rv)
    {
        super.onAttachedToRecyclerView(rv);
    }

    public void addAll(ArrayList<ShoppingListObject> items)
    {
        if(_shoppingItems != null) {
            int oldsize = _shoppingItems.size();
            _shoppingItems.clear();
            _shoppingItems.addAll(items);

            notifyItemRangeRemoved(0, oldsize);
            notifyItemRangeInserted(0, _shoppingItems.size());
        }
    }

    public ShoppingListObject GetItem(int pos)
    {
        return _shoppingItems.get(pos);
    }

    @Override
    public void OnSuccess(RequestType requestType, Object s)
    {
        switch (requestType)
        {
            case PATCH:
                completeAlreadyPressed = false;
                _shoppingItems.get(selected).Purchased = !_shoppingItems.get(selected).Purchased;
                notifyItemChanged(selected);
                break;
            case DELETE:
                Toast.makeText(_context, "Item deleted", Toast.LENGTH_SHORT).show();
                _deletecallback.onItemDeleted();
                break;
        }
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    {
        switch (requestType)
        {
            case PATCH:
                completeAlreadyPressed = false;
                Toast.makeText(_context, "Failed to update shopping item.", Toast.LENGTH_SHORT).show();
                break;
            case DELETE:
                Toast.makeText(_context, "Failed to delete", Toast.LENGTH_SHORT).show();
                _deletecallback.onItemDeleted();
                break;
        }
    }
}
