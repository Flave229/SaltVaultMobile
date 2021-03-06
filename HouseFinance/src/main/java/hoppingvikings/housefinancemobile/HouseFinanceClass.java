package hoppingvikings.housefinancemobile;

import android.app.Application;
import android.content.Intent;

import hoppingvikings.housefinancemobile.Notifications.DaggerNotificationWrapperComponent;
import hoppingvikings.housefinancemobile.Services.SaltVault.Bills.BillComponent;
import hoppingvikings.housefinancemobile.Services.SaltVault.Bills.DaggerBillComponent;
import hoppingvikings.housefinancemobile.Services.SaltVault.House.DaggerHouseholdComponent;
import hoppingvikings.housefinancemobile.Services.SaltVault.House.HouseholdComponent;
import hoppingvikings.housefinancemobile.Services.SaltVault.Shopping.DaggerShoppingComponent;
import hoppingvikings.housefinancemobile.Services.SaltVault.Shopping.ShoppingComponent;
import hoppingvikings.housefinancemobile.Services.SaltVault.ToDo.DaggerToDoComponent;
import hoppingvikings.housefinancemobile.Services.SaltVault.ToDo.ToDoComponent;
import hoppingvikings.housefinancemobile.Services.SaltVault.User.DaggerSessionPersisterComponent;
import hoppingvikings.housefinancemobile.Services.SaltVault.User.DaggerUserComponent;
import hoppingvikings.housefinancemobile.Services.SaltVault.User.UserComponent;
import hoppingvikings.housefinancemobile.Notifications.NotificationWrapperComponent;
import hoppingvikings.housefinancemobile.Services.SaltVault.User.SessionPersisterComponent;

public class HouseFinanceClass extends Application implements AppServiceBinder.OnBindInterface
{
    private static NotificationWrapperComponent _notificationComponent;
    private static SessionPersisterComponent _sessionComponent;
    private static HouseholdComponent _householdComponent;
    private static UserComponent _userComponent;
    private static BillComponent _billComponent;
    private static ShoppingComponent _shoppingComponent;
    private static ToDoComponent _toDoComponent;

    @Override
    public void OnBind()
    {
        // AppServiceBinder._service.ShowNotification("Started", NotificationManager.IMPORTANCE_DEFAULT);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        AppServiceBinder.owner = this;

        _notificationComponent = DaggerNotificationWrapperComponent.builder().build();
        _sessionComponent = DaggerSessionPersisterComponent.builder().build();
        _householdComponent = DaggerHouseholdComponent.builder()
                .sessionPersisterComponent(_sessionComponent)
                .build();
        _userComponent = DaggerUserComponent.builder()
                .sessionPersisterComponent(_sessionComponent)
                .build();
        _billComponent = DaggerBillComponent.builder()
                .sessionPersisterComponent(_sessionComponent)
                .build();
        _shoppingComponent = DaggerShoppingComponent.builder()
                .sessionPersisterComponent(_sessionComponent)
                .build();
        _toDoComponent = DaggerToDoComponent.builder()
                .sessionPersisterComponent(_sessionComponent)
                .build();

        Thread serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent service = new Intent(getApplicationContext(), BackgroundService.class);
                startService(service);
                AppServiceBinder.ConnectToBackgroundService(getApplicationContext());
            }
        }, "HouseFinanceServiceThread");
        serviceThread.setPriority(Thread.NORM_PRIORITY);
        serviceThread.start();
    }

    public static NotificationWrapperComponent GetNotificationWrapperComponent()
    {
        return _notificationComponent;
    }

    public static SessionPersisterComponent GetSessionPersisterComponent()
    {
        return _sessionComponent;
    }

    public static HouseholdComponent GetHouseholdComponent()
    {
        return _householdComponent;
    }

    public static UserComponent GetUserComponent()
    {
        return _userComponent;
    }

    public static BillComponent GetBillComponent()
    {
        return _billComponent;
    }

    public static ShoppingComponent GetShoppingComponent()
    {
        return _shoppingComponent;
    }

    public static ToDoComponent GetToDoComponent()
    {
        return _toDoComponent;
    }

    @Override
    public void OnUnbind()
    {
        AppServiceBinder.DisconnectBackgroundService(this);
    }
}
