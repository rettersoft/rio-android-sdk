import com.rettermobile.rio.Rio
import com.rettermobile.rio.cloud.RioCallMethodOptions
import com.rettermobile.rio.cloud.RioCloudObject
import com.rettermobile.rio.cloud.RioGetCloudObjectOptions

sealed class RioModels {
    class AnythingArrayValue(val value: List<Any?>) : RioModels()
    class BoolValue(val value: Boolean) : RioModels()
    class DoubleValue(val value: Double) : RioModels()
    class IntegerValue(val value: Long) : RioModels()
    class RioModelsClassValue(val value: RioModelsClass) : RioModels()
    class StringValue(val value: String) : RioModels()
    class NullValue() : RioModels()
}

data class RioModelsClass(
    val addImageToCategoryInput: AddImageToCategoryInput? = null,
    val address: Address? = null,
    val attributeInput: AttributeInput? = null,
    val configInput: ConfigInput? = null,

    /**
     * User coordinate
     */
    val findNearestStoreInput: FindNearestStoreInput? = null,

    /**
     * A client calls Homepage class's getHome method with this input.
     */
    val getHomepageInput: GetHomepageInput? = null,

    /**
     * Home page model describing what to show on clients home page.
     */
    val getHomepageOutput: GetHomepageOutput? = null,

    val getImageInput: GetImageInput? = null,
    val getMsisdnAuthInput: GetMsisdnAuthInput? = null,

    /**
     * An object representing a banner in home page of the app.
     */
    val homepageBanner: HomepageBanner? = null,

    val homepageBaseModel: HomepageBaseModel? = null,

    /**
     * An object representing a story in home page of the app.
     */
    val homepageStory: HomepageStory? = null,

    val isSignUpCompleteOutput: IsSignUpCompleteOutput? = null,
    val location: LocationClass? = null,
    val product: Product? = null,
    val productInitInput: ProductInitInput? = null,
    val productUpdateInput: ProductUpdateInput? = null,
    val queryUserInput: QueryUserInput? = null,
    val queryUserOutput: QueryUserOutput? = null,
    val removeAddressInput: RemoveAddressInput? = null,
    val removeAttributeInput: RemoveAttributeInput? = null,
    val removeCategoryInput: RemoveCategoryInput? = null,
    val sayHelloInput: SayHelloInput? = null,
    val searchListingProductsInput: SearchListingProductsInput? = null,
    val signinInput: SigninInput? = null,
    val signUpInput: SignUpInput? = null,
    val storeDecreaseStockInput: List<StoreDecreaseStockInputElement>? = null,
    val storeGetInstanceIdInput: StoreGetInstanceIdInput? = null,
    val storeGetProductBySku: List<String>? = null,
    val storeIncreaseDecreaseItem: StoreIncreaseDecreaseItem? = null,
    val storeIncreaseStockInput: List<StoreDecreaseStockInputElement>? = null,
    val storeInfo: StoreInfo? = null,
    val storeProduct: StoreProduct? = null,
    val storeProductDeleteInput: StoreProductDeleteInput? = null,
    val storeProductPrice: StoreProductPrice? = null,
    val storeProductPriceSyncObject: StoreProductPriceSyncObject? = null,
    val storeProductStockSyncObject: StoreProductStockSyncObject? = null,
    val storeSetProductPrice: StoreSetProductPrice? = null,
    val storeSetProductStock: StoreSetProductStock? = null,
    val storeSkuInput: StoreSkuInput? = null,
    val storeUpsertFeedURLs: StoreUpsertFeedURLs? = null,
    val storeUpsertOrderInput: StoreUpsertOrderInput? = null,
    val syncStoresInput: SyncStoresInput? = null,
    val updateConfigInput: UpdateConfigInput? = null,
    val updateEmailInput: UpdateEmailInput? = null,
    val updatePasswordInput: UpdatePasswordInput? = null,
    val updateProductInput: UpdateProductInput? = null,
    val updateProductOutput: UpdateProductOutput? = null,
    val updateProfileInput: UpdateProfileInput? = null,
    val uploadImageInput: UploadImageInput? = null,
    val upsertCategoryInput: UpsertCategoryInput? = null,

    /**
     * Ecom client calls Homepage class's upsertHomepage method with this input.
     */
    val upsertHomepageInput: UpsertHomepageInput? = null,

    val upsertProductInput: UpsertProductInput? = null,
    val userSignUpInput: UserSignUpInput? = null,
    val validateOtpInput: ValidateOtpInput? = null,
    val validateOtpOutput: ValidateOtpOutput? = null
)

data class AddImageToCategoryInput(
    val content: String,
    val id: String
)

data class Address(
    val addressId: String? = null,
    val alternativeRecipient: String? = null,
    val building: String? = null,
    val city: String? = null,
    val companyName: String? = null,
    val country: String? = null,
    val createdAt: Double? = null,
    val district: String? = null,
    val door: String? = null,
    val floor: String? = null,
    val identityNo: String? = null,
    val invoiceType: InvoiceType? = null,
    val location: Location? = null,
    val nameSurname: String? = null,
    val neighborhood: String? = null,
    val phoneNumber: String? = null,
    val postalCode: String? = null,
    val street: String? = null,
    val taxNo: String? = null,
    val taxOffice: String? = null,
    val text: String? = null,
    val title: String? = null,
    val updatedAt: Double? = null
)

enum class InvoiceType {
    Corporate,
    EArsiv,
    Individual,
    None
}

data class Location(
    val lat: Double,
    val lng: Double
)

data class AttributeInput(
    val createdAt: Double? = null,
    val createdBy: String? = null,
    val updatedAt: Double? = null,
    val updatedBy: String? = null,
    val default: Any? = null,
    val id: String,
    val isLocalized: Boolean? = null,
    val isRequired: Boolean? = null,
    val listing: Boolean? = null,
    val name: Map<String, String>,
    val options: List<String>? = null,
    val type: Type
)

enum class Type {
    Number,
    Record,
    TypeArray,
    TypeBoolean,
    TypeEnum,
    TypeString
}

data class ConfigInput(
    val locales: List<Locale>,
    val rootCategoryId: String? = null
)

enum class Locale {
    EnUS,
    RuRU,
    TrTR
}

/**
 * User coordinate
 */
data class FindNearestStoreInput(
    val lat: Double,
    val lng: Double
)

/**
 * A client calls Homepage class's getHome method with this input.
 */
data class GetHomepageInput(
    val cacheDuration: Double? = null,
    val platform: Platform,
    val segment: String? = null,
    val storeId: String? = null
)

enum class Platform {
    Android,
    Ios,
    Web
}

/**
 * Home page model describing what to show on clients home page.
 */
data class GetHomepageOutput(
    val banners: List<BannerElement>,
    val stories: List<StoryElement>
)

/**
 * An object representing a banner in home page of the app.
 */
data class BannerElement(
    val imageURL: String,
    val title: String,
    val excludeSegments: List<String>? = null,
    val excludeStores: List<String>? = null,
    val expireDate: Double? = null,
    val includeSegments: List<String>? = null,
    val includeStores: List<String>? = null
)

/**
 * An object representing a story in home page of the app.
 */
data class StoryElement(
    val imageURL: String,
    val isStarred: Boolean,
    val title: String,
    val excludeSegments: List<String>? = null,
    val excludeStores: List<String>? = null,
    val expireDate: Double? = null,
    val includeSegments: List<String>? = null,
    val includeStores: List<String>? = null
)

data class GetImageInput(
    val fit: Fit? = null,
    val height: Double? = null,
    val id: String,
    val quality: Double? = null,
    val width: Double? = null
)

enum class Fit {
    Contain,
    Cover,
    Fill,
    Inside,
    Outside
}

data class GetMsisdnAuthInput(
    val msisdn: String
)

/**
 * An object representing a banner in home page of the app.
 */
data class HomepageBanner(
    val imageURL: String,
    val title: String,
    val excludeSegments: List<String>? = null,
    val excludeStores: List<String>? = null,
    val expireDate: Double? = null,
    val includeSegments: List<String>? = null,
    val includeStores: List<String>? = null
)

data class HomepageBaseModel(
    val excludeSegments: List<String>? = null,
    val excludeStores: List<String>? = null,
    val expireDate: Double? = null,
    val includeSegments: List<String>? = null,
    val includeStores: List<String>? = null
)

/**
 * An object representing a story in home page of the app.
 */
data class HomepageStory(
    val imageURL: String,
    val isStarred: Boolean,
    val title: String,
    val excludeSegments: List<String>? = null,
    val excludeStores: List<String>? = null,
    val expireDate: Double? = null,
    val includeSegments: List<String>? = null,
    val includeStores: List<String>? = null
)

data class IsSignUpCompleteOutput(
    val isSignupComplete: Boolean,
    val userId: String
)

data class LocationClass(
    val lat: Double,
    val lng: Double
)

data class Product(
    val createdAt: Double? = null,
    val createdBy: String? = null,
    val updatedAt: Double? = null,
    val updatedBy: String? = null,
    val attributes: Map<String, Any?>? = null,
    val hash: String? = null,
    val id: String,
    val images: List<ProductImage>? = null,
    val isEnabled: Boolean? = null
)

data class ProductImage(
    val id: String,
    val tags: List<String>? = null,
    val url: String? = null
)

data class ProductInitInput(
    val id: String
)

data class ProductUpdateInput(
    val attributes: Map<String, Any?>
)

data class QueryUserInput(
    val msisdn: String
)

data class QueryUserOutput(
    val customerId: String
)

data class RemoveAddressInput(
    val addressId: String
)

data class RemoveAttributeInput(
    val id: String
)

data class RemoveCategoryInput(
    val id: String
)

data class SayHelloInput(
    /**
     * Age in years which must be equal to or greater than zero.
     */
    val age: Long? = null,

    /**
     * The person's first name.
     */
    val firstName: String? = null,

    /**
     * The person's last name.
     */
    val lastName: String? = null
)

data class SearchListingProductsInput(
    val from: Double? = null,
    val q: String? = null,
    val size: Double? = null
)

data class SignUpInput(
    val birthdate: String,
    val contactCampaign: Boolean? = null,
    val email: String,
    val firstName: String,
    val gender: Gender,
    val lastName: String,
    val signupToken: String
)

enum class Gender {
    Female,
    Male
}

data class SigninInput(
    val password: String
)

data class StoreDecreaseStockInputElement(
    val quantity: Long,
    val sku: String
)

data class StoreGetInstanceIdInput(
    val storeId: String
)

data class StoreIncreaseDecreaseItem(
    val quantity: Long,
    val sku: String
)

data class StoreInfo(
    val isDeleted: Boolean? = null,
    val lat: Double,
    val lng: Double,
    val radiusInMeter: Long,
    val storeId: String
)

data class StoreProduct(
    val isDisabled: Boolean? = null,
    val prices: Prices? = null,
    val stock: Long? = null
)

class Prices()

data class StoreProductDeleteInput(
    val sku: String
)

class StoreProductPrice()

data class StoreProductPriceSyncObject(
    val prices: Prices,
    val sku: String,
    val storeId: String
)

data class StoreProductStockSyncObject(
    val sku: String,
    val stock: Long,
    val storeId: String
)

data class StoreSetProductPrice(
    val prices: Prices,
    val sku: String
)

data class StoreSetProductStock(
    val sku: String,
    val stock: Long
)

data class StoreSkuInput(
    val sku: String
)

data class StoreUpsertFeedURLs(
    val pricesURL: String? = null,
    val stocksURL: String? = null,
    val storesURL: String? = null
)

data class StoreUpsertOrderInput(
    val orderId: String? = null,
    val status: String? = null
)

data class SyncStoresInput(
    /**
     * Url method should be get and url response should be json string
     */
    val url: String
)

data class UpdateConfigInput(
    val locales: List<Locale>
)

data class UpdateEmailInput(
    val email: String
)

data class UpdatePasswordInput(
    val oldPassword: String,
    val password: String
)

data class UpdateProductInput(
    val createdAt: Double? = null,
    val createdBy: String? = null,
    val hash: String? = null,
    val updatedAt: Double? = null,
    val updatedBy: String? = null,
    val attributes: Map<String, Any?>? = null,
    val categories: List<UpdateProductInputCategory>? = null,
    val id: String,
    val images: List<UpdateProductInputImage>? = null,
    val isEnabled: Boolean? = null
)

data class UpdateProductInputCategory(
    val id: String,
    val name: Map<String, String>
)

data class UpdateProductInputImage(
    val id: String,
    val tags: List<String>? = null,
    val url: String? = null
)

data class UpdateProductOutput(
    val createdAt: Double? = null,
    val createdBy: String? = null,
    val hash: String? = null,
    val updatedAt: Double? = null,
    val updatedBy: String? = null,
    val attributes: Map<String, Any?>? = null,
    val categories: List<UpdateProductOutputCategory>? = null,
    val id: String,
    val images: List<UpdateProductOutputImage>? = null,
    val isEnabled: Boolean? = null
)

data class UpdateProductOutputCategory(
    val id: String,
    val name: Map<String, String>
)

data class UpdateProductOutputImage(
    val id: String,
    val tags: List<String>? = null,
    val url: String? = null
)

data class UpdateProfileInput(
    val birthdate: String,
    val contactCampaign: Boolean? = null,
    val firstName: String,
    val gender: Gender,
    val lastName: String
)

data class UploadImageInput(
    val content: String,
    val tags: List<String>? = null
)

data class UpsertCategoryInput(
    val description: Map<String, String>? = null,
    val id: String,
    val images: List<UpsertCategoryInputImage>? = null,
    val name: Map<String, String>,
    val parentId: String? = null,
    val sortingOrder: Double? = null
)

data class UpsertCategoryInputImage(
    val id: String,
    val tags: List<String>? = null,
    val url: String? = null
)

/**
 * Ecom client calls Homepage class's upsertHomepage method with this input.
 */
data class UpsertHomepageInput(
    val banners: List<BannerElement>? = null,
    val platform: Platform,
    val stories: List<StoryElement>
)

data class UpsertProductInput(
    val createdAt: Double? = null,
    val createdBy: String? = null,
    val updatedAt: Double? = null,
    val updatedBy: String? = null,
    val attributes: Map<String, Any?>? = null,
    val hash: String? = null,
    val id: String,
    val images: List<UpsertProductInputImage>? = null,
    val isEnabled: Boolean? = null
)

data class UpsertProductInputImage(
    val id: String,
    val tags: List<String>? = null,
    val url: String? = null
)

data class UserSignUpInput(
    val birthdate: String,
    val contactCampaign: Boolean? = null,
    val email: String,
    val firstName: String,
    val gender: Gender,
    val lastName: String,
    val msisdn: String
)

data class ValidateOtpInput(
    val otp: String
)

data class ValidateOtpOutput(
    val authStatus: AuthStatus,
    val customToken: String? = null,
    val signupToken: String? = null
)

enum class AuthStatus {
    AuthFailedInvalidOtp,
    SignupRequired,
    Success,
    TooManyAttempts
}

class RioClasses {
    class BackofficeUser private constructor(obj: RioCloudObject) {
        var _obj: RioCloudObject = obj

        companion object {
            fun getInstance(
                rio: Rio, options: RioGetCloudObjectOptions? = null, onSuccess: ((BackofficeUser) -> Unit)? = null,
                onError: ((Throwable?) -> Unit)? = null
            ) {
                val newOptions = options ?: RioGetCloudObjectOptions(
                    classId = "BackofficeUser"
                )

                rio.getCloudObject(newOptions, onSuccess = {
                    onSuccess?.invoke(BackofficeUser(it))
                }, onError = {
                    onError?.invoke(it)
                })
            }
        }

        fun createUser(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "createUser",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun signIn(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "signIn",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun updatePassword(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "updatePassword",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }
    }

    class CDH private constructor(obj: RioCloudObject) {
        var _obj: RioCloudObject = obj

        companion object {
            fun getInstance(
                rio: Rio, options: RioGetCloudObjectOptions? = null, onSuccess: ((CDH) -> Unit)? = null,
                onError: ((Throwable?) -> Unit)? = null
            ) {
                val newOptions = options ?: RioGetCloudObjectOptions(
                    classId = "CDH"
                )

                rio.getCloudObject(newOptions, onSuccess = {
                    onSuccess?.invoke(CDH(it))
                }, onError = {
                    onError?.invoke(it)
                })
            }
        }

        fun query(
            input: QueryUserInput, options: RioCallMethodOptions? = null,
            onSuccess: ((QueryUserOutput?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "query",
                body = input
            )

            _obj.call<QueryUserOutput>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }
    }

    class Homepage private constructor(obj: RioCloudObject) {
        var _obj: RioCloudObject = obj

        companion object {
            fun getInstance(
                rio: Rio, options: RioGetCloudObjectOptions? = null, onSuccess: ((Homepage) -> Unit)? = null,
                onError: ((Throwable?) -> Unit)? = null
            ) {
                val newOptions = options ?: RioGetCloudObjectOptions(
                    classId = "Homepage"
                )

                rio.getCloudObject(newOptions, onSuccess = {
                    onSuccess?.invoke(Homepage(it))
                }, onError = {
                    onError?.invoke(it)
                })
            }
        }

        fun getHome(
            input: GetHomepageInput, options: RioCallMethodOptions? = null,
            onSuccess: ((GetHomepageOutput?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "getHome",
                body = input
            )

            _obj.call<GetHomepageOutput>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun upsertHomeData(
            input: UpsertHomepageInput, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "upsertHomeData",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }
    }

    class MsisdnAuthenticator private constructor(obj: RioCloudObject) {
        var _obj: RioCloudObject = obj

        companion object {
            fun getInstance(
                rio: Rio, options: RioGetCloudObjectOptions? = null, onSuccess: ((MsisdnAuthenticator) -> Unit)? = null,
                onError: ((Throwable?) -> Unit)? = null
            ) {
                val newOptions = options ?: RioGetCloudObjectOptions(
                    classId = "MsisdnAuthenticator"
                )

                rio.getCloudObject(newOptions, onSuccess = {
                    onSuccess?.invoke(MsisdnAuthenticator(it))
                }, onError = {
                    onError?.invoke(it)
                })
            }
        }

        fun sendOtp(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "sendOtp",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun validateOtp(
            input: ValidateOtpInput, options: RioCallMethodOptions? = null,
            onSuccess: ((ValidateOtpOutput?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "validateOtp",
                body = input
            )

            _obj.call<ValidateOtpOutput>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun signup(
            input: SignUpInput, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "signup",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }
    }

    class Places private constructor(obj: RioCloudObject) {
        var _obj: RioCloudObject = obj

        companion object {
            fun getInstance(
                rio: Rio, options: RioGetCloudObjectOptions? = null, onSuccess: ((Places) -> Unit)? = null,
                onError: ((Throwable?) -> Unit)? = null
            ) {
                val newOptions = options ?: RioGetCloudObjectOptions(
                    classId = "Places"
                )

                rio.getCloudObject(newOptions, onSuccess = {
                    onSuccess?.invoke(Places(it))
                }, onError = {
                    onError?.invoke(it)
                })
            }
        }

        fun getPlaces(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "getPlaces",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }
    }

    class Product private constructor(obj: RioCloudObject) {
        var _obj: RioCloudObject = obj

        companion object {
            fun getInstance(
                rio: Rio, options: RioGetCloudObjectOptions? = null, onSuccess: ((Product) -> Unit)? = null,
                onError: ((Throwable?) -> Unit)? = null
            ) {
                val newOptions = options ?: RioGetCloudObjectOptions(
                    classId = "Product"
                )

                rio.getCloudObject(newOptions, onSuccess = {
                    onSuccess?.invoke(Product(it))
                }, onError = {
                    onError?.invoke(it)
                })
            }
        }

        fun get(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "get",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun update(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "update",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun uploadImage(
            input: UploadImageInput, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "uploadImage",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun getImage(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "getImage",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }
    }

    class ProductManager private constructor(obj: RioCloudObject) {
        var _obj: RioCloudObject = obj

        companion object {
            fun getInstance(
                rio: Rio, options: RioGetCloudObjectOptions? = null, onSuccess: ((ProductManager) -> Unit)? = null,
                onError: ((Throwable?) -> Unit)? = null
            ) {
                val newOptions = options ?: RioGetCloudObjectOptions(
                    classId = "ProductManager"
                )

                rio.getCloudObject(newOptions, onSuccess = {
                    onSuccess?.invoke(ProductManager(it))
                }, onError = {
                    onError?.invoke(it)
                })
            }
        }

        fun getConfig(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "getConfig",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun updateConfig(
            input: ConfigInput, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "updateConfig",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun upsertAttribute(
            input: AttributeInput, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "upsertAttribute",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun removeAttribute(
            input: RemoveAttributeInput, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "removeAttribute",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun listAttributes(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "listAttributes",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun upsertCategory(
            input: UpsertCategoryInput, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "upsertCategory",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun removeCategory(
            input: RemoveCategoryInput, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "removeCategory",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun addImageToCategory(
            input: AddImageToCategoryInput, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "addImageToCategory",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun listCategories(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "listCategories",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun getCategoryTree(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "getCategoryTree",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun getCategoryImage(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "getCategoryImage",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun syncProduct(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "syncProduct",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun listProducts(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "listProducts",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun importProducts(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "importProducts",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun syncProducts(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "syncProducts",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun search(
            input: SearchListingProductsInput, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "search",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun triggerExport(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "triggerExport",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun exporter(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "exporter",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun getFile(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "getFile",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }
    }

    class User private constructor(obj: RioCloudObject) {
        var _obj: RioCloudObject = obj

        companion object {
            fun getInstance(
                rio: Rio, options: RioGetCloudObjectOptions? = null, onSuccess: ((User) -> Unit)? = null,
                onError: ((Throwable?) -> Unit)? = null
            ) {
                val newOptions = options ?: RioGetCloudObjectOptions(
                    classId = "User"
                )

                rio.getCloudObject(newOptions, onSuccess = {
                    onSuccess?.invoke(User(it))
                }, onError = {
                    onError?.invoke(it)
                })
            }
        }

        fun updateEmail(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "updateEmail",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun validateEmail(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "validateEmail",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun signup(
            input: UserSignUpInput, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "signup",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun updateProfile(
            input: UpdateProfileInput, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "updateProfile",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun isSignupComplete(
            input: Any? = null, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "isSignupComplete",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun upsertAddress(
            input: Address, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "upsertAddress",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }

        fun removeAddress(
            input: RemoveAddressInput, options: RioCallMethodOptions? = null,
            onSuccess: ((Any?) -> Unit)? = null,
            onError: ((Throwable?) -> Unit)? = null
        ) {
            val newOptions = options ?: RioCallMethodOptions(
                method = "removeAddress",
                body = input
            )

            _obj.call<Any>(newOptions, onSuccess = {
                onSuccess?.invoke(it.body)
            }, onError = {
                onError?.invoke(it)
            })
        }
    }
}
    
