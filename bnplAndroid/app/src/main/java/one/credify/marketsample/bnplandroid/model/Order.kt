package one.credify.marketsample.bnplandroid.model
import com.google.gson.annotations.SerializedName
import one.credify.sdk.core.model.CurrencyType

class PaymentRecipient(
    @SerializedName("name")
    val name: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("branch")
    val branch: String,
    @SerializedName("bank")
    val bank: String,
)

class TotalAmount(
    @SerializedName("value")
    val value: String,
    @SerializedName("currency")
    val currency: CurrencyType,
)

class OrderLine(
    @SerializedName("name")
    val name: String,
    @SerializedName("reference_id")
    val referenceId: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("product_url")
    val productUrl: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: TotalAmount,
    @SerializedName("subtotal")
    val subtotal: TotalAmount,
    @SerializedName("measurement_unit")
    val measurementUnit: String,
)

class Order(
    @SerializedName("reference_id")
    val referenceId: String,
    @SerializedName("total_amount")
    val totalAmount: TotalAmount,
    @SerializedName("order_lines")
    val orderLines: List<OrderLine>,
    @SerializedName("payment_recipient")
    val paymentRecipient: PaymentRecipient,
    @SerializedName("user_id")
    val userId: String,
)