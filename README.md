# Clover app implementing Order Receipt Printing + NotificationReceiver

### Usage

1. Create a new Clover app, upload the APK found in `app/release/` to your dev dashboard
2. Make sure the debug cable is attached to your Clover device and computer
3. Start the app
4. Grab the Auth Token from LogCat
5. Make a POST request to `{{baseUrl}}/v3/apps/{{appId}}/merchants/{{merchantId}}/notifications` with the following JSON body (replace `orderId` with an actual order ID):

```JSON
{
	"event": "test_notification",
	"data": "{{orderId}}"
}
```
