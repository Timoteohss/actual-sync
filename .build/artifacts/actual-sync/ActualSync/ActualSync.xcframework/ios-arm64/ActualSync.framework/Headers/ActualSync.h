#import <Foundation/NSArray.h>
#import <Foundation/NSDictionary.h>
#import <Foundation/NSError.h>
#import <Foundation/NSObject.h>
#import <Foundation/NSSet.h>
#import <Foundation/NSString.h>
#import <Foundation/NSValue.h>

@class ActualSyncAccount, ActualSyncAccounts, ActualSyncActualDatabaseCompanion, ActualSyncActualDatabaseQueries, ActualSyncBudgetCategory, ActualSyncBudgetFile, ActualSyncBudgetFileManager, ActualSyncBudgetMonth, ActualSyncCategories, ActualSyncCategory, ActualSyncCategoryGroup, ActualSyncCategory_groups, ActualSyncClockManager, ActualSyncClockState, ActualSyncClockStateCompanion, ActualSyncDatabaseDriverFactory, ActualSyncEncryptedData, ActualSyncEncryptedDataCompanion, ActualSyncFileData, ActualSyncFileDataCompanion, ActualSyncGetLastTimestamp, ActualSyncGetSyncMetadata, ActualSyncKotlinAbstractCoroutineContextElement, ActualSyncKotlinAbstractCoroutineContextKey<B, E>, ActualSyncKotlinArray<T>, ActualSyncKotlinByteArray, ActualSyncKotlinByteIterator, ActualSyncKotlinCancellationException, ActualSyncKotlinEnum<E>, ActualSyncKotlinEnumCompanion, ActualSyncKotlinException, ActualSyncKotlinIllegalStateException, ActualSyncKotlinKTypeProjection, ActualSyncKotlinKTypeProjectionCompanion, ActualSyncKotlinKVariance, ActualSyncKotlinNothing, ActualSyncKotlinPair<__covariant A, __covariant B>, ActualSyncKotlinRuntimeException, ActualSyncKotlinThrowable, ActualSyncKotlinUnit, ActualSyncKotlinx_coroutines_coreCoroutineDispatcher, ActualSyncKotlinx_coroutines_coreCoroutineDispatcherKey, ActualSyncKotlinx_io_coreBuffer, ActualSyncKotlinx_serialization_coreSerialKind, ActualSyncKotlinx_serialization_coreSerializersModule, ActualSyncKtor_client_coreHttpClient, ActualSyncKtor_client_coreHttpClientCall, ActualSyncKtor_client_coreHttpClientCallCompanion, ActualSyncKtor_client_coreHttpClientConfig<T>, ActualSyncKtor_client_coreHttpClientEngineConfig, ActualSyncKtor_client_coreHttpReceivePipeline, ActualSyncKtor_client_coreHttpReceivePipelinePhases, ActualSyncKtor_client_coreHttpRequestBuilder, ActualSyncKtor_client_coreHttpRequestBuilderCompanion, ActualSyncKtor_client_coreHttpRequestData, ActualSyncKtor_client_coreHttpRequestPipeline, ActualSyncKtor_client_coreHttpRequestPipelinePhases, ActualSyncKtor_client_coreHttpResponse, ActualSyncKtor_client_coreHttpResponseContainer, ActualSyncKtor_client_coreHttpResponseData, ActualSyncKtor_client_coreHttpResponsePipeline, ActualSyncKtor_client_coreHttpResponsePipelinePhases, ActualSyncKtor_client_coreHttpSendPipeline, ActualSyncKtor_client_coreHttpSendPipelinePhases, ActualSyncKtor_client_coreProxyConfig, ActualSyncKtor_eventsEventDefinition<T>, ActualSyncKtor_eventsEvents, ActualSyncKtor_httpContentType, ActualSyncKtor_httpContentTypeCompanion, ActualSyncKtor_httpHeaderValueParam, ActualSyncKtor_httpHeaderValueWithParameters, ActualSyncKtor_httpHeaderValueWithParametersCompanion, ActualSyncKtor_httpHeadersBuilder, ActualSyncKtor_httpHttpMethod, ActualSyncKtor_httpHttpMethodCompanion, ActualSyncKtor_httpHttpProtocolVersion, ActualSyncKtor_httpHttpProtocolVersionCompanion, ActualSyncKtor_httpHttpStatusCode, ActualSyncKtor_httpHttpStatusCodeCompanion, ActualSyncKtor_httpOutgoingContent, ActualSyncKtor_httpURLBuilder, ActualSyncKtor_httpURLBuilderCompanion, ActualSyncKtor_httpURLProtocol, ActualSyncKtor_httpURLProtocolCompanion, ActualSyncKtor_httpUrl, ActualSyncKtor_httpUrlCompanion, ActualSyncKtor_utilsAttributeKey<T>, ActualSyncKtor_utilsGMTDate, ActualSyncKtor_utilsGMTDateCompanion, ActualSyncKtor_utilsMonth, ActualSyncKtor_utilsMonthCompanion, ActualSyncKtor_utilsPipeline<TSubject, TContext>, ActualSyncKtor_utilsPipelinePhase, ActualSyncKtor_utilsStringValuesBuilderImpl, ActualSyncKtor_utilsTypeInfo, ActualSyncKtor_utilsWeekDay, ActualSyncKtor_utilsWeekDayCompanion, ActualSyncListFilesResponse, ActualSyncListFilesResponseCompanion, ActualSyncLoginData, ActualSyncLoginDataCompanion, ActualSyncLoginResponse, ActualSyncLoginResponseCompanion, ActualSyncMerkle, ActualSyncMessage, ActualSyncMessageCompanion, ActualSyncMessageEnvelope, ActualSyncMessageEnvelopeCompanion, ActualSyncMessageEnvelope_, ActualSyncMessages_crdt, ActualSyncMurmurHash3, ActualSyncMutableClock, ActualSyncMutableClockCompanion, ActualSyncNewTransaction, ActualSyncNotes, ActualSyncPayee_mapping, ActualSyncPayees, ActualSyncPendingChangeDetail, ActualSyncProtobuf, ActualSyncRules, ActualSyncRuntimeAfterVersion, ActualSyncRuntimeBaseTransacterImpl, ActualSyncRuntimeExecutableQuery<__covariant RowType>, ActualSyncRuntimeQuery<__covariant RowType>, ActualSyncRuntimeTransacterImpl, ActualSyncRuntimeTransacterTransaction, ActualSyncSchedule_next_date, ActualSyncSchedules, ActualSyncSyncClock, ActualSyncSyncEngine, ActualSyncSyncManager, ActualSyncSyncRequest, ActualSyncSyncRequestCompanion, ActualSyncSyncResponse, ActualSyncSyncResponseCompanion, ActualSyncSyncResponse_, ActualSyncSyncResult, ActualSyncSyncResultError, ActualSyncSyncResultSuccess, ActualSyncSync_metadata, ActualSyncTimestamp, ActualSyncTimestampCompanion, ActualSyncTransaction, ActualSyncTransactions, ActualSyncTrieNode, ActualSyncTrieNodeCompanion, ActualSyncZero_budgets;

@protocol ActualSyncActualDatabase, ActualSyncKotlinAnnotation, ActualSyncKotlinAutoCloseable, ActualSyncKotlinComparable, ActualSyncKotlinContinuation, ActualSyncKotlinContinuationInterceptor, ActualSyncKotlinCoroutineContext, ActualSyncKotlinCoroutineContextElement, ActualSyncKotlinCoroutineContextKey, ActualSyncKotlinFunction, ActualSyncKotlinIterator, ActualSyncKotlinKAnnotatedElement, ActualSyncKotlinKClass, ActualSyncKotlinKClassifier, ActualSyncKotlinKDeclarationContainer, ActualSyncKotlinKType, ActualSyncKotlinMapEntry, ActualSyncKotlinSequence, ActualSyncKotlinSuspendFunction2, ActualSyncKotlinx_coroutines_coreChildHandle, ActualSyncKotlinx_coroutines_coreChildJob, ActualSyncKotlinx_coroutines_coreCoroutineScope, ActualSyncKotlinx_coroutines_coreDisposableHandle, ActualSyncKotlinx_coroutines_coreJob, ActualSyncKotlinx_coroutines_coreParentJob, ActualSyncKotlinx_coroutines_coreRunnable, ActualSyncKotlinx_coroutines_coreSelectClause, ActualSyncKotlinx_coroutines_coreSelectClause0, ActualSyncKotlinx_coroutines_coreSelectInstance, ActualSyncKotlinx_io_coreRawSink, ActualSyncKotlinx_io_coreRawSource, ActualSyncKotlinx_io_coreSink, ActualSyncKotlinx_io_coreSource, ActualSyncKotlinx_serialization_coreCompositeDecoder, ActualSyncKotlinx_serialization_coreCompositeEncoder, ActualSyncKotlinx_serialization_coreDecoder, ActualSyncKotlinx_serialization_coreDeserializationStrategy, ActualSyncKotlinx_serialization_coreEncoder, ActualSyncKotlinx_serialization_coreKSerializer, ActualSyncKotlinx_serialization_coreSerialDescriptor, ActualSyncKotlinx_serialization_coreSerializationStrategy, ActualSyncKotlinx_serialization_coreSerializersModuleCollector, ActualSyncKtor_client_coreHttpClientEngine, ActualSyncKtor_client_coreHttpClientEngineCapability, ActualSyncKtor_client_coreHttpClientPlugin, ActualSyncKtor_client_coreHttpRequest, ActualSyncKtor_httpHeaders, ActualSyncKtor_httpHttpMessage, ActualSyncKtor_httpHttpMessageBuilder, ActualSyncKtor_httpParameters, ActualSyncKtor_httpParametersBuilder, ActualSyncKtor_ioByteReadChannel, ActualSyncKtor_ioCloseable, ActualSyncKtor_utilsAttributes, ActualSyncKtor_utilsStringValues, ActualSyncKtor_utilsStringValuesBuilder, ActualSyncRuntimeCloseable, ActualSyncRuntimeQueryListener, ActualSyncRuntimeQueryResult, ActualSyncRuntimeSqlCursor, ActualSyncRuntimeSqlDriver, ActualSyncRuntimeSqlPreparedStatement, ActualSyncRuntimeSqlSchema, ActualSyncRuntimeTransacter, ActualSyncRuntimeTransacterBase, ActualSyncRuntimeTransactionCallbacks, ActualSyncRuntimeTransactionWithReturn, ActualSyncRuntimeTransactionWithoutReturn;

NS_ASSUME_NONNULL_BEGIN
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunknown-warning-option"
#pragma clang diagnostic ignored "-Wincompatible-property-type"
#pragma clang diagnostic ignored "-Wnullability"

#pragma push_macro("_Nullable_result")
#if !__has_feature(nullability_nullable_result)
#undef _Nullable_result
#define _Nullable_result _Nullable
#endif

__attribute__((swift_name("KotlinBase")))
@interface ActualSyncBase : NSObject
- (instancetype)init __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
+ (void)initialize __attribute__((objc_requires_super));
@end

@interface ActualSyncBase (ActualSyncBaseCopying) <NSCopying>
@end

__attribute__((swift_name("KotlinMutableSet")))
@interface ActualSyncMutableSet<ObjectType> : NSMutableSet<ObjectType>
@end

__attribute__((swift_name("KotlinMutableDictionary")))
@interface ActualSyncMutableDictionary<KeyType, ObjectType> : NSMutableDictionary<KeyType, ObjectType>
@end

@interface NSError (NSErrorActualSyncKotlinException)
@property (readonly) id _Nullable kotlinException;
@end

__attribute__((swift_name("KotlinNumber")))
@interface ActualSyncNumber : NSNumber
- (instancetype)initWithChar:(char)value __attribute__((unavailable));
- (instancetype)initWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
- (instancetype)initWithShort:(short)value __attribute__((unavailable));
- (instancetype)initWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
- (instancetype)initWithInt:(int)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
- (instancetype)initWithLong:(long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
- (instancetype)initWithLongLong:(long long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
- (instancetype)initWithFloat:(float)value __attribute__((unavailable));
- (instancetype)initWithDouble:(double)value __attribute__((unavailable));
- (instancetype)initWithBool:(BOOL)value __attribute__((unavailable));
- (instancetype)initWithInteger:(NSInteger)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
+ (instancetype)numberWithChar:(char)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
+ (instancetype)numberWithShort:(short)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
+ (instancetype)numberWithInt:(int)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
+ (instancetype)numberWithLong:(long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
+ (instancetype)numberWithLongLong:(long long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
+ (instancetype)numberWithFloat:(float)value __attribute__((unavailable));
+ (instancetype)numberWithDouble:(double)value __attribute__((unavailable));
+ (instancetype)numberWithBool:(BOOL)value __attribute__((unavailable));
+ (instancetype)numberWithInteger:(NSInteger)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
@end

__attribute__((swift_name("KotlinByte")))
@interface ActualSyncByte : ActualSyncNumber
- (instancetype)initWithChar:(char)value;
+ (instancetype)numberWithChar:(char)value;
@end

__attribute__((swift_name("KotlinUByte")))
@interface ActualSyncUByte : ActualSyncNumber
- (instancetype)initWithUnsignedChar:(unsigned char)value;
+ (instancetype)numberWithUnsignedChar:(unsigned char)value;
@end

__attribute__((swift_name("KotlinShort")))
@interface ActualSyncShort : ActualSyncNumber
- (instancetype)initWithShort:(short)value;
+ (instancetype)numberWithShort:(short)value;
@end

__attribute__((swift_name("KotlinUShort")))
@interface ActualSyncUShort : ActualSyncNumber
- (instancetype)initWithUnsignedShort:(unsigned short)value;
+ (instancetype)numberWithUnsignedShort:(unsigned short)value;
@end

__attribute__((swift_name("KotlinInt")))
@interface ActualSyncInt : ActualSyncNumber
- (instancetype)initWithInt:(int)value;
+ (instancetype)numberWithInt:(int)value;
@end

__attribute__((swift_name("KotlinUInt")))
@interface ActualSyncUInt : ActualSyncNumber
- (instancetype)initWithUnsignedInt:(unsigned int)value;
+ (instancetype)numberWithUnsignedInt:(unsigned int)value;
@end

__attribute__((swift_name("KotlinLong")))
@interface ActualSyncLong : ActualSyncNumber
- (instancetype)initWithLongLong:(long long)value;
+ (instancetype)numberWithLongLong:(long long)value;
@end

__attribute__((swift_name("KotlinULong")))
@interface ActualSyncULong : ActualSyncNumber
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value;
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value;
@end

__attribute__((swift_name("KotlinFloat")))
@interface ActualSyncFloat : ActualSyncNumber
- (instancetype)initWithFloat:(float)value;
+ (instancetype)numberWithFloat:(float)value;
@end

__attribute__((swift_name("KotlinDouble")))
@interface ActualSyncDouble : ActualSyncNumber
- (instancetype)initWithDouble:(double)value;
+ (instancetype)numberWithDouble:(double)value;
@end

__attribute__((swift_name("KotlinBoolean")))
@interface ActualSyncBoolean : ActualSyncNumber
- (instancetype)initWithBool:(BOOL)value;
+ (instancetype)numberWithBool:(BOOL)value;
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Account")))
@interface ActualSyncAccount : ActualSyncBase
- (instancetype)initWithId:(NSString *)id name:(NSString *)name type:(NSString * _Nullable)type offBudget:(BOOL)offBudget closed:(BOOL)closed balance:(int64_t)balance __attribute__((swift_name("init(id:name:type:offBudget:closed:balance:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncAccount *)doCopyId:(NSString *)id name:(NSString *)name type:(NSString * _Nullable)type offBudget:(BOOL)offBudget closed:(BOOL)closed balance:(int64_t)balance __attribute__((swift_name("doCopy(id:name:type:offBudget:closed:balance:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int64_t balance __attribute__((swift_name("balance")));
@property (readonly) BOOL closed __attribute__((swift_name("closed")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) BOOL offBudget __attribute__((swift_name("offBudget")));
@property (readonly) NSString * _Nullable type __attribute__((swift_name("type")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ActualClient")))
@interface ActualSyncActualClient : ActualSyncBase
- (instancetype)initWithServerUrl:(NSString *)serverUrl dataDir:(NSString *)dataDir __attribute__((swift_name("init(serverUrl:dataDir:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)createTransactionTransaction:(ActualSyncNewTransaction *)transaction completionHandler:(void (^)(id _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("createTransaction(transaction:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)downloadBudgetSyncId:(NSString *)syncId password:(NSString * _Nullable)password completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("downloadBudget(syncId:password:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)getAccountsWithCompletionHandler:(void (^)(id _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("getAccounts(completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)getBudgetMonthMonth:(NSString *)month completionHandler:(void (^)(id _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("getBudgetMonth(month:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)getBudgetsWithCompletionHandler:(void (^)(NSArray<ActualSyncBudgetFile *> * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("getBudgets(completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)getCategoriesWithCompletionHandler:(void (^)(id _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("getCategories(completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)getTransactionsAccountId:(NSString *)accountId startDate:(NSString *)startDate endDate:(NSString *)endDate completionHandler:(void (^)(id _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("getTransactions(accountId:startDate:endDate:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)doInitWithCompletionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("doInit(completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)loginPassword:(NSString *)password completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("login(password:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)shutdownWithCompletionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("shutdown(completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)syncWithCompletionHandler:(void (^)(id _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("sync(completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BudgetCategory")))
@interface ActualSyncBudgetCategory : ActualSyncBase
- (instancetype)initWithCategoryId:(NSString *)categoryId budgeted:(int64_t)budgeted spent:(int64_t)spent balance:(int64_t)balance carryover:(BOOL)carryover __attribute__((swift_name("init(categoryId:budgeted:spent:balance:carryover:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncBudgetCategory *)doCopyCategoryId:(NSString *)categoryId budgeted:(int64_t)budgeted spent:(int64_t)spent balance:(int64_t)balance carryover:(BOOL)carryover __attribute__((swift_name("doCopy(categoryId:budgeted:spent:balance:carryover:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int64_t balance __attribute__((swift_name("balance")));
@property (readonly) int64_t budgeted __attribute__((swift_name("budgeted")));
@property (readonly) BOOL carryover __attribute__((swift_name("carryover")));
@property (readonly) NSString *categoryId __attribute__((swift_name("categoryId")));
@property (readonly) int64_t spent __attribute__((swift_name("spent")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BudgetMonth")))
@interface ActualSyncBudgetMonth : ActualSyncBase
- (instancetype)initWithMonth:(NSString *)month categories:(NSArray<ActualSyncBudgetCategory *> *)categories __attribute__((swift_name("init(month:categories:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncBudgetMonth *)doCopyMonth:(NSString *)month categories:(NSArray<ActualSyncBudgetCategory *> *)categories __attribute__((swift_name("doCopy(month:categories:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSArray<ActualSyncBudgetCategory *> *categories __attribute__((swift_name("categories")));
@property (readonly) NSString *month __attribute__((swift_name("month")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Category")))
@interface ActualSyncCategory : ActualSyncBase
- (instancetype)initWithId:(NSString *)id name:(NSString *)name groupId:(NSString *)groupId isIncome:(BOOL)isIncome hidden:(BOOL)hidden __attribute__((swift_name("init(id:name:groupId:isIncome:hidden:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncCategory *)doCopyId:(NSString *)id name:(NSString *)name groupId:(NSString *)groupId isIncome:(BOOL)isIncome hidden:(BOOL)hidden __attribute__((swift_name("doCopy(id:name:groupId:isIncome:hidden:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *groupId __attribute__((swift_name("groupId")));
@property (readonly) BOOL hidden __attribute__((swift_name("hidden")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) BOOL isIncome __attribute__((swift_name("isIncome")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CategoryGroup")))
@interface ActualSyncCategoryGroup : ActualSyncBase
- (instancetype)initWithId:(NSString *)id name:(NSString *)name isIncome:(BOOL)isIncome hidden:(BOOL)hidden categories:(NSArray<ActualSyncCategory *> *)categories __attribute__((swift_name("init(id:name:isIncome:hidden:categories:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncCategoryGroup *)doCopyId:(NSString *)id name:(NSString *)name isIncome:(BOOL)isIncome hidden:(BOOL)hidden categories:(NSArray<ActualSyncCategory *> *)categories __attribute__((swift_name("doCopy(id:name:isIncome:hidden:categories:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSArray<ActualSyncCategory *> *categories __attribute__((swift_name("categories")));
@property (readonly) BOOL hidden __attribute__((swift_name("hidden")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) BOOL isIncome __attribute__((swift_name("isIncome")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("NewTransaction")))
@interface ActualSyncNewTransaction : ActualSyncBase
- (instancetype)initWithAccountId:(NSString *)accountId date:(NSString *)date amount:(int64_t)amount payeeName:(NSString * _Nullable)payeeName payeeId:(NSString * _Nullable)payeeId categoryId:(NSString * _Nullable)categoryId notes:(NSString * _Nullable)notes cleared:(BOOL)cleared __attribute__((swift_name("init(accountId:date:amount:payeeName:payeeId:categoryId:notes:cleared:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncNewTransaction *)doCopyAccountId:(NSString *)accountId date:(NSString *)date amount:(int64_t)amount payeeName:(NSString * _Nullable)payeeName payeeId:(NSString * _Nullable)payeeId categoryId:(NSString * _Nullable)categoryId notes:(NSString * _Nullable)notes cleared:(BOOL)cleared __attribute__((swift_name("doCopy(accountId:date:amount:payeeName:payeeId:categoryId:notes:cleared:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *accountId __attribute__((swift_name("accountId")));
@property (readonly) int64_t amount __attribute__((swift_name("amount")));
@property (readonly) NSString * _Nullable categoryId __attribute__((swift_name("categoryId")));
@property (readonly) BOOL cleared __attribute__((swift_name("cleared")));
@property (readonly) NSString *date __attribute__((swift_name("date")));
@property (readonly) NSString * _Nullable notes __attribute__((swift_name("notes")));
@property (readonly) NSString * _Nullable payeeId __attribute__((swift_name("payeeId")));
@property (readonly) NSString * _Nullable payeeName __attribute__((swift_name("payeeName")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Transaction")))
@interface ActualSyncTransaction : ActualSyncBase
- (instancetype)initWithId:(NSString *)id accountId:(NSString *)accountId date:(NSString *)date amount:(int64_t)amount payeeId:(NSString * _Nullable)payeeId payeeName:(NSString * _Nullable)payeeName categoryId:(NSString * _Nullable)categoryId notes:(NSString * _Nullable)notes cleared:(BOOL)cleared reconciled:(BOOL)reconciled __attribute__((swift_name("init(id:accountId:date:amount:payeeId:payeeName:categoryId:notes:cleared:reconciled:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncTransaction *)doCopyId:(NSString *)id accountId:(NSString *)accountId date:(NSString *)date amount:(int64_t)amount payeeId:(NSString * _Nullable)payeeId payeeName:(NSString * _Nullable)payeeName categoryId:(NSString * _Nullable)categoryId notes:(NSString * _Nullable)notes cleared:(BOOL)cleared reconciled:(BOOL)reconciled __attribute__((swift_name("doCopy(id:accountId:date:amount:payeeId:payeeName:categoryId:notes:cleared:reconciled:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *accountId __attribute__((swift_name("accountId")));
@property (readonly) int64_t amount __attribute__((swift_name("amount")));
@property (readonly) NSString * _Nullable categoryId __attribute__((swift_name("categoryId")));
@property (readonly) BOOL cleared __attribute__((swift_name("cleared")));
@property (readonly) NSString *date __attribute__((swift_name("date")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) NSString * _Nullable notes __attribute__((swift_name("notes")));
@property (readonly) NSString * _Nullable payeeId __attribute__((swift_name("payeeId")));
@property (readonly) NSString * _Nullable payeeName __attribute__((swift_name("payeeName")));
@property (readonly) BOOL reconciled __attribute__((swift_name("reconciled")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ClockManager")))
@interface ActualSyncClockManager : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)clockManager __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncClockManager *shared __attribute__((swift_name("shared")));
- (ActualSyncSyncClock *)deserializeData:(NSString *)data __attribute__((swift_name("deserialize(data:)")));
- (ActualSyncSyncClock * _Nullable)getClock __attribute__((swift_name("getClock()")));
- (ActualSyncSyncClock *)makeClockTimestamp:(ActualSyncTimestamp *)timestamp merkle:(ActualSyncTrieNode *)merkle __attribute__((swift_name("makeClock(timestamp:merkle:)")));
- (NSString *)serializeClock:(ActualSyncSyncClock *)clock __attribute__((swift_name("serialize(clock:)")));
- (void)setClockNewClock:(ActualSyncSyncClock *)newClock __attribute__((swift_name("setClock(newClock:)")));
@end


/**
 * @note annotations
 *   kotlinx.serialization.Serializable
*/
__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ClockState")))
@interface ActualSyncClockState : ActualSyncBase
- (instancetype)initWithTimestamp:(NSString *)timestamp merkle:(ActualSyncTrieNode *)merkle __attribute__((swift_name("init(timestamp:merkle:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncClockStateCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncClockState *)doCopyTimestamp:(NSString *)timestamp merkle:(ActualSyncTrieNode *)merkle __attribute__((swift_name("doCopy(timestamp:merkle:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncTrieNode *merkle __attribute__((swift_name("merkle")));
@property (readonly) NSString *timestamp __attribute__((swift_name("timestamp")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ClockState.Companion")))
@interface ActualSyncClockStateCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncClockStateCompanion *shared __attribute__((swift_name("shared")));
- (id<ActualSyncKotlinx_serialization_coreKSerializer>)serializer __attribute__((swift_name("serializer()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Merkle")))
@interface ActualSyncMerkle : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)merkle __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncMerkle *shared __attribute__((swift_name("shared")));
- (ActualSyncTrieNode *)buildTimestamps:(NSArray<ActualSyncTimestamp *> *)timestamps __attribute__((swift_name("build(timestamps:)")));
- (NSString *)debugTrie:(ActualSyncTrieNode *)trie key:(NSString *)key indent:(int32_t)indent __attribute__((swift_name("debug(trie:key:indent:)")));
- (ActualSyncTrieNode *)deserializeData:(NSString *)data __attribute__((swift_name("deserialize(data:)")));
- (ActualSyncLong * _Nullable)diffTrie1:(ActualSyncTrieNode *)trie1 trie2:(ActualSyncTrieNode *)trie2 __attribute__((swift_name("diff(trie1:trie2:)")));
- (ActualSyncTrieNode *)emptyTrie __attribute__((swift_name("emptyTrie()")));
- (ActualSyncTrieNode *)insertTrie:(ActualSyncTrieNode *)trie timestamp:(ActualSyncTimestamp *)timestamp __attribute__((swift_name("insert(trie:timestamp:)")));
- (int64_t)keyToTimestampKey:(NSString *)key __attribute__((swift_name("keyToTimestamp(key:)")));
- (ActualSyncTrieNode *)pruneTrie:(ActualSyncTrieNode *)trie n:(int32_t)n __attribute__((swift_name("prune(trie:n:)")));
- (NSString *)serializeTrie:(ActualSyncTrieNode *)trie __attribute__((swift_name("serialize(trie:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MurmurHash3")))
@interface ActualSyncMurmurHash3 : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)murmurHash3 __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncMurmurHash3 *shared __attribute__((swift_name("shared")));
- (int32_t)hash32Data:(ActualSyncKotlinByteArray *)data seed:(int32_t)seed __attribute__((swift_name("hash32(data:seed:)")));
- (int32_t)hash32Data:(NSString *)data seed_:(int32_t)seed __attribute__((swift_name("hash32(data:seed_:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MutableClock")))
@interface ActualSyncMutableClock : ActualSyncBase
- (instancetype)initWithMillis:(int64_t)millis counter:(int32_t)counter node:(NSString *)node __attribute__((swift_name("init(millis:counter:node:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncMutableClockCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncTimestamp *)recvMsg:(ActualSyncTimestamp *)msg __attribute__((swift_name("recv(msg:)")));
- (ActualSyncTimestamp *)send __attribute__((swift_name("send()")));
- (ActualSyncTimestamp *)toTimestamp __attribute__((swift_name("toTimestamp()")));
@property int32_t counter __attribute__((swift_name("counter")));
@property int64_t millis __attribute__((swift_name("millis")));
@property (readonly) NSString *node __attribute__((swift_name("node")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MutableClock.Companion")))
@interface ActualSyncMutableClockCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncMutableClockCompanion *shared __attribute__((swift_name("shared")));
- (ActualSyncMutableClock *)fromTimestamp:(ActualSyncTimestamp *)timestamp __attribute__((swift_name("from(timestamp:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SyncClock")))
@interface ActualSyncSyncClock : ActualSyncBase
- (instancetype)initWithTimestamp:(ActualSyncMutableClock *)timestamp merkle:(ActualSyncTrieNode *)merkle __attribute__((swift_name("init(timestamp:merkle:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncSyncClock *)doCopyTimestamp:(ActualSyncMutableClock *)timestamp merkle:(ActualSyncTrieNode *)merkle __attribute__((swift_name("doCopy(timestamp:merkle:)")));
- (ActualSyncTimestamp *)current __attribute__((swift_name("current()")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (ActualSyncTimestamp *)recvMsg:(ActualSyncTimestamp *)msg __attribute__((swift_name("recv(msg:)")));
- (ActualSyncTimestamp *)send __attribute__((swift_name("send()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property ActualSyncTrieNode *merkle __attribute__((swift_name("merkle")));
@property (readonly) ActualSyncMutableClock *timestamp __attribute__((swift_name("timestamp")));
@end

__attribute__((swift_name("KotlinComparable")))
@protocol ActualSyncKotlinComparable
@required
- (int32_t)compareToOther:(id _Nullable)other __attribute__((swift_name("compareTo(other:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Timestamp")))
@interface ActualSyncTimestamp : ActualSyncBase <ActualSyncKotlinComparable>
- (instancetype)initWithMillis:(int64_t)millis counter:(int32_t)counter node:(NSString *)node __attribute__((swift_name("init(millis:counter:node:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncTimestampCompanion *companion __attribute__((swift_name("companion")));
- (int32_t)compareToOther:(ActualSyncTimestamp *)other __attribute__((swift_name("compareTo(other:)")));
- (ActualSyncTimestamp *)doCopyMillis:(int64_t)millis counter:(int32_t)counter node:(NSString *)node __attribute__((swift_name("doCopy(millis:counter:node:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (int32_t)hash_ __attribute__((swift_name("hash_()")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int32_t counter __attribute__((swift_name("counter")));
@property (readonly) int64_t millis __attribute__((swift_name("millis")));
@property (readonly) NSString *node __attribute__((swift_name("node")));
@end

__attribute__((swift_name("KotlinThrowable")))
@interface ActualSyncKotlinThrowable : ActualSyncBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));

/**
 * @note annotations
 *   kotlin.experimental.ExperimentalNativeApi
*/
- (ActualSyncKotlinArray<NSString *> *)getStackTrace __attribute__((swift_name("getStackTrace()")));
- (void)printStackTrace __attribute__((swift_name("printStackTrace()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncKotlinThrowable * _Nullable cause __attribute__((swift_name("cause")));
@property (readonly) NSString * _Nullable message __attribute__((swift_name("message")));
- (NSError *)asError __attribute__((swift_name("asError()")));
@end

__attribute__((swift_name("KotlinException")))
@interface ActualSyncKotlinException : ActualSyncKotlinThrowable
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Timestamp.ClockDriftError")))
@interface ActualSyncTimestampClockDriftError : ActualSyncKotlinException
- (instancetype)initWithMessage:(NSString *)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithCause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Timestamp.Companion")))
@interface ActualSyncTimestampCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncTimestampCompanion *shared __attribute__((swift_name("shared")));
- (NSString *)makeClientId __attribute__((swift_name("makeClientId()")));
- (ActualSyncTimestamp * _Nullable)parseTimestamp:(NSString *)timestamp __attribute__((swift_name("parse(timestamp:)")));
- (NSString *)sinceIsoString:(NSString *)isoString __attribute__((swift_name("since(isoString:)")));
@property (readonly) ActualSyncTimestamp *MAX __attribute__((swift_name("MAX")));
@property (readonly) ActualSyncTimestamp *ZERO __attribute__((swift_name("ZERO")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Timestamp.InvalidError")))
@interface ActualSyncTimestampInvalidError : ActualSyncKotlinException
- (instancetype)initWithTimestamp:(NSString *)timestamp __attribute__((swift_name("init(timestamp:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Timestamp.OverflowError")))
@interface ActualSyncTimestampOverflowError : ActualSyncKotlinException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end


/**
 * @note annotations
 *   kotlinx.serialization.Serializable
*/
__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("TrieNode")))
@interface ActualSyncTrieNode : ActualSyncBase
- (instancetype)initWithHash:(int32_t)hash children:(ActualSyncMutableDictionary<id, ActualSyncTrieNode *> *)children __attribute__((swift_name("init(hash:children:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncTrieNodeCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncTrieNode *)doCopyHash:(int32_t)hash children:(ActualSyncMutableDictionary<id, ActualSyncTrieNode *> *)children __attribute__((swift_name("doCopy(hash:children:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (ActualSyncTrieNode * _Nullable)getKey:(unichar)key __attribute__((swift_name("get(key:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSArray<id> *)keys __attribute__((swift_name("keys()")));
- (void)setKey:(unichar)key node:(ActualSyncTrieNode *)node __attribute__((swift_name("set(key:node:)")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncMutableDictionary<id, ActualSyncTrieNode *> *children __attribute__((swift_name("children")));
@property (readonly, getter=hash_) int32_t hash __attribute__((swift_name("hash")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("TrieNode.Companion")))
@interface ActualSyncTrieNodeCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncTrieNodeCompanion *shared __attribute__((swift_name("shared")));
- (id<ActualSyncKotlinx_serialization_coreKSerializer>)serializer __attribute__((swift_name("serializer()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Accounts")))
@interface ActualSyncAccounts : ActualSyncBase
- (instancetype)initWithId:(NSString *)id name:(NSString * _Nullable)name offbudget:(ActualSyncLong * _Nullable)offbudget closed:(ActualSyncLong * _Nullable)closed tombstone:(ActualSyncLong * _Nullable)tombstone sort_order:(ActualSyncDouble * _Nullable)sort_order __attribute__((swift_name("init(id:name:offbudget:closed:tombstone:sort_order:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncAccounts *)doCopyId:(NSString *)id name:(NSString * _Nullable)name offbudget:(ActualSyncLong * _Nullable)offbudget closed:(ActualSyncLong * _Nullable)closed tombstone:(ActualSyncLong * _Nullable)tombstone sort_order:(ActualSyncDouble * _Nullable)sort_order __attribute__((swift_name("doCopy(id:name:offbudget:closed:tombstone:sort_order:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncLong * _Nullable closed __attribute__((swift_name("closed")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) NSString * _Nullable name __attribute__((swift_name("name")));
@property (readonly) ActualSyncLong * _Nullable offbudget __attribute__((swift_name("offbudget")));
@property (readonly) ActualSyncDouble * _Nullable sort_order __attribute__((swift_name("sort_order")));
@property (readonly) ActualSyncLong * _Nullable tombstone __attribute__((swift_name("tombstone")));
@end

__attribute__((swift_name("RuntimeTransacterBase")))
@protocol ActualSyncRuntimeTransacterBase
@required
@end

__attribute__((swift_name("RuntimeTransacter")))
@protocol ActualSyncRuntimeTransacter <ActualSyncRuntimeTransacterBase>
@required
- (void)transactionNoEnclosing:(BOOL)noEnclosing body:(void (^)(id<ActualSyncRuntimeTransactionWithoutReturn>))body __attribute__((swift_name("transaction(noEnclosing:body:)")));
- (id _Nullable)transactionWithResultNoEnclosing:(BOOL)noEnclosing bodyWithReturn:(id _Nullable (^)(id<ActualSyncRuntimeTransactionWithReturn>))bodyWithReturn __attribute__((swift_name("transactionWithResult(noEnclosing:bodyWithReturn:)")));
@end

__attribute__((swift_name("ActualDatabase")))
@protocol ActualSyncActualDatabase <ActualSyncRuntimeTransacter>
@required
@property (readonly) ActualSyncActualDatabaseQueries *actualDatabaseQueries __attribute__((swift_name("actualDatabaseQueries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ActualDatabaseCompanion")))
@interface ActualSyncActualDatabaseCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncActualDatabaseCompanion *shared __attribute__((swift_name("shared")));
- (id<ActualSyncActualDatabase>)invokeDriver:(id<ActualSyncRuntimeSqlDriver>)driver __attribute__((swift_name("invoke(driver:)")));
@property (readonly) id<ActualSyncRuntimeSqlSchema> Schema __attribute__((swift_name("Schema")));
@end

__attribute__((swift_name("RuntimeBaseTransacterImpl")))
@interface ActualSyncRuntimeBaseTransacterImpl : ActualSyncBase
- (instancetype)initWithDriver:(id<ActualSyncRuntimeSqlDriver>)driver __attribute__((swift_name("init(driver:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (NSString *)createArgumentsCount:(int32_t)count __attribute__((swift_name("createArguments(count:)")));

/**
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (void)notifyQueriesIdentifier:(int32_t)identifier tableProvider:(void (^)(ActualSyncKotlinUnit *(^)(NSString *)))tableProvider __attribute__((swift_name("notifyQueries(identifier:tableProvider:)")));

/**
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (id _Nullable)postTransactionCleanupTransaction:(ActualSyncRuntimeTransacterTransaction *)transaction enclosing:(ActualSyncRuntimeTransacterTransaction * _Nullable)enclosing thrownException:(ActualSyncKotlinThrowable * _Nullable)thrownException returnValue:(id _Nullable)returnValue __attribute__((swift_name("postTransactionCleanup(transaction:enclosing:thrownException:returnValue:)")));

/**
 * @note This property has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
@property (readonly) id<ActualSyncRuntimeSqlDriver> driver __attribute__((swift_name("driver")));
@end

__attribute__((swift_name("RuntimeTransacterImpl")))
@interface ActualSyncRuntimeTransacterImpl : ActualSyncRuntimeBaseTransacterImpl <ActualSyncRuntimeTransacter>
- (instancetype)initWithDriver:(id<ActualSyncRuntimeSqlDriver>)driver __attribute__((swift_name("init(driver:)"))) __attribute__((objc_designated_initializer));
- (void)transactionNoEnclosing:(BOOL)noEnclosing body:(void (^)(id<ActualSyncRuntimeTransactionWithoutReturn>))body __attribute__((swift_name("transaction(noEnclosing:body:)")));
- (id _Nullable)transactionWithResultNoEnclosing:(BOOL)noEnclosing bodyWithReturn:(id _Nullable (^)(id<ActualSyncRuntimeTransactionWithReturn>))bodyWithReturn __attribute__((swift_name("transactionWithResult(noEnclosing:bodyWithReturn:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ActualDatabaseQueries")))
@interface ActualSyncActualDatabaseQueries : ActualSyncRuntimeTransacterImpl
- (instancetype)initWithDriver:(id<ActualSyncRuntimeSqlDriver>)driver __attribute__((swift_name("init(driver:)"))) __attribute__((objc_designated_initializer));
- (void)clearAccounts __attribute__((swift_name("clearAccounts()")));
- (void)clearBudgets __attribute__((swift_name("clearBudgets()")));
- (void)clearCategories __attribute__((swift_name("clearCategories()")));
- (void)clearCategoryGroups __attribute__((swift_name("clearCategoryGroups()")));
- (void)clearMessages __attribute__((swift_name("clearMessages()")));
- (void)clearMetadata __attribute__((swift_name("clearMetadata()")));
- (void)clearPayees __attribute__((swift_name("clearPayees()")));
- (void)clearTransactions __attribute__((swift_name("clearTransactions()")));
- (ActualSyncRuntimeQuery<ActualSyncLong *> *)countAllAccounts __attribute__((swift_name("countAllAccounts()")));
- (ActualSyncRuntimeQuery<ActualSyncLong *> *)countAllCategories __attribute__((swift_name("countAllCategories()")));
- (ActualSyncRuntimeQuery<ActualSyncLong *> *)countAllCategoryGroups __attribute__((swift_name("countAllCategoryGroups()")));
- (ActualSyncRuntimeQuery<ActualSyncLong *> *)countAllPayees __attribute__((swift_name("countAllPayees()")));
- (ActualSyncRuntimeQuery<ActualSyncLong *> *)countAllTransactions __attribute__((swift_name("countAllTransactions()")));
- (ActualSyncRuntimeQuery<ActualSyncAccounts *> *)getAccountByIdId:(NSString *)id __attribute__((swift_name("getAccountById(id:)")));
- (ActualSyncRuntimeQuery<id> *)getAccountByIdId:(NSString *)id mapper:(id (^)(NSString *, NSString * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable, ActualSyncDouble * _Nullable))mapper __attribute__((swift_name("getAccountById(id:mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncAccounts *> *)getAccounts __attribute__((swift_name("getAccounts()")));
- (ActualSyncRuntimeQuery<id> *)getAccountsMapper:(id (^)(NSString *, NSString * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable, ActualSyncDouble * _Nullable))mapper __attribute__((swift_name("getAccounts(mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncAccounts *> *)getAllAccounts __attribute__((swift_name("getAllAccounts()")));
- (ActualSyncRuntimeQuery<id> *)getAllAccountsMapper:(id (^)(NSString *, NSString * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable, ActualSyncDouble * _Nullable))mapper __attribute__((swift_name("getAllAccounts(mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncCategories *> *)getAllCategories __attribute__((swift_name("getAllCategories()")));
- (ActualSyncRuntimeQuery<id> *)getAllCategoriesMapper:(id (^)(NSString *, NSString * _Nullable, ActualSyncLong * _Nullable, NSString * _Nullable, ActualSyncDouble * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable))mapper __attribute__((swift_name("getAllCategories(mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncCategory_groups *> *)getAllCategoryGroups __attribute__((swift_name("getAllCategoryGroups()")));
- (ActualSyncRuntimeQuery<id> *)getAllCategoryGroupsMapper:(id (^)(NSString *, NSString * _Nullable, ActualSyncLong * _Nullable, ActualSyncDouble * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable))mapper __attribute__((swift_name("getAllCategoryGroups(mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncPayees *> *)getAllPayees __attribute__((swift_name("getAllPayees()")));
- (ActualSyncRuntimeQuery<id> *)getAllPayeesMapper:(id (^)(NSString *, NSString * _Nullable, NSString * _Nullable, ActualSyncLong * _Nullable))mapper __attribute__((swift_name("getAllPayees(mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncTransactions *> *)getAllTransactions __attribute__((swift_name("getAllTransactions()")));
- (ActualSyncRuntimeQuery<id> *)getAllTransactionsMapper:(id (^)(NSString *, NSString * _Nullable, NSString * _Nullable, ActualSyncLong * _Nullable, NSString * _Nullable, NSString * _Nullable, ActualSyncLong * _Nullable, ActualSyncDouble * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable))mapper __attribute__((swift_name("getAllTransactions(mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncZero_budgets *> *)getBudgetByIdId:(NSString *)id __attribute__((swift_name("getBudgetById(id:)")));
- (ActualSyncRuntimeQuery<id> *)getBudgetByIdId:(NSString *)id mapper:(id (^)(NSString *, ActualSyncLong *, NSString *, ActualSyncLong *, ActualSyncLong *, ActualSyncLong * _Nullable, ActualSyncLong *))mapper __attribute__((swift_name("getBudgetById(id:mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncZero_budgets *> *)getBudgetForCategoryCategory:(NSString *)category __attribute__((swift_name("getBudgetForCategory(category:)")));
- (ActualSyncRuntimeQuery<id> *)getBudgetForCategoryCategory:(NSString *)category mapper:(id (^)(NSString *, ActualSyncLong *, NSString *, ActualSyncLong *, ActualSyncLong *, ActualSyncLong * _Nullable, ActualSyncLong *))mapper __attribute__((swift_name("getBudgetForCategory(category:mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncZero_budgets *> *)getBudgetForMonthMonth:(int64_t)month __attribute__((swift_name("getBudgetForMonth(month:)")));
- (ActualSyncRuntimeQuery<id> *)getBudgetForMonthMonth:(int64_t)month mapper:(id (^)(NSString *, ActualSyncLong *, NSString *, ActualSyncLong *, ActualSyncLong *, ActualSyncLong * _Nullable, ActualSyncLong *))mapper __attribute__((swift_name("getBudgetForMonth(month:mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncCategories *> *)getCategories __attribute__((swift_name("getCategories()")));
- (ActualSyncRuntimeQuery<id> *)getCategoriesMapper:(id (^)(NSString *, NSString * _Nullable, ActualSyncLong * _Nullable, NSString * _Nullable, ActualSyncDouble * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable))mapper __attribute__((swift_name("getCategories(mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncCategories *> *)getCategoriesByGroupCat_group:(NSString * _Nullable)cat_group __attribute__((swift_name("getCategoriesByGroup(cat_group:)")));
- (ActualSyncRuntimeQuery<id> *)getCategoriesByGroupCat_group:(NSString * _Nullable)cat_group mapper:(id (^)(NSString *, NSString * _Nullable, ActualSyncLong * _Nullable, NSString * _Nullable, ActualSyncDouble * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable))mapper __attribute__((swift_name("getCategoriesByGroup(cat_group:mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncCategories *> *)getCategoryByIdId:(NSString *)id __attribute__((swift_name("getCategoryById(id:)")));
- (ActualSyncRuntimeQuery<id> *)getCategoryByIdId:(NSString *)id mapper:(id (^)(NSString *, NSString * _Nullable, ActualSyncLong * _Nullable, NSString * _Nullable, ActualSyncDouble * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable))mapper __attribute__((swift_name("getCategoryById(id:mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncCategory_groups *> *)getCategoryGroupByIdId:(NSString *)id __attribute__((swift_name("getCategoryGroupById(id:)")));
- (ActualSyncRuntimeQuery<id> *)getCategoryGroupByIdId:(NSString *)id mapper:(id (^)(NSString *, NSString * _Nullable, ActualSyncLong * _Nullable, ActualSyncDouble * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable))mapper __attribute__((swift_name("getCategoryGroupById(id:mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncCategory_groups *> *)getCategoryGroups __attribute__((swift_name("getCategoryGroups()")));
- (ActualSyncRuntimeQuery<id> *)getCategoryGroupsMapper:(id (^)(NSString *, NSString * _Nullable, ActualSyncLong * _Nullable, ActualSyncDouble * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable))mapper __attribute__((swift_name("getCategoryGroups(mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncGetLastTimestamp *> *)getLastTimestamp __attribute__((swift_name("getLastTimestamp()")));
- (ActualSyncRuntimeQuery<id> *)getLastTimestampMapper:(id (^)(NSString * _Nullable))mapper __attribute__((swift_name("getLastTimestamp(mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncMessages_crdt *> *)getMessagesSinceTimestamp:(NSString *)timestamp __attribute__((swift_name("getMessagesSince(timestamp:)")));
- (ActualSyncRuntimeQuery<id> *)getMessagesSinceTimestamp:(NSString *)timestamp mapper:(id (^)(ActualSyncLong *, NSString *, NSString *, NSString *, NSString *, ActualSyncKotlinByteArray *))mapper __attribute__((swift_name("getMessagesSince(timestamp:mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncPayees *> *)getPayeeByIdId:(NSString *)id __attribute__((swift_name("getPayeeById(id:)")));
- (ActualSyncRuntimeQuery<id> *)getPayeeByIdId:(NSString *)id mapper:(id (^)(NSString *, NSString * _Nullable, NSString * _Nullable, ActualSyncLong * _Nullable))mapper __attribute__((swift_name("getPayeeById(id:mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncPayee_mapping *> *)getPayeeMappingByIdId:(NSString *)id __attribute__((swift_name("getPayeeMappingById(id:)")));
- (ActualSyncRuntimeQuery<id> *)getPayeeMappingByIdId:(NSString *)id mapper:(id (^)(NSString *, NSString * _Nullable))mapper __attribute__((swift_name("getPayeeMappingById(id:mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncPayees *> *)getPayees __attribute__((swift_name("getPayees()")));
- (ActualSyncRuntimeQuery<id> *)getPayeesMapper:(id (^)(NSString *, NSString * _Nullable, NSString * _Nullable, ActualSyncLong * _Nullable))mapper __attribute__((swift_name("getPayees(mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncGetSyncMetadata *> *)getSyncMetadataKey:(NSString *)key __attribute__((swift_name("getSyncMetadata(key:)")));
- (ActualSyncRuntimeQuery<id> *)getSyncMetadataKey:(NSString *)key mapper:(id (^)(NSString * _Nullable))mapper __attribute__((swift_name("getSyncMetadata(key:mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncTransactions *> *)getTransactionByIdId:(NSString *)id __attribute__((swift_name("getTransactionById(id:)")));
- (ActualSyncRuntimeQuery<id> *)getTransactionByIdId:(NSString *)id mapper:(id (^)(NSString *, NSString * _Nullable, NSString * _Nullable, ActualSyncLong * _Nullable, NSString * _Nullable, NSString * _Nullable, ActualSyncLong * _Nullable, ActualSyncDouble * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable))mapper __attribute__((swift_name("getTransactionById(id:mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncTransactions *> *)getTransactionsByAccountAcct:(NSString * _Nullable)acct __attribute__((swift_name("getTransactionsByAccount(acct:)")));
- (ActualSyncRuntimeQuery<id> *)getTransactionsByAccountAcct:(NSString * _Nullable)acct mapper:(id (^)(NSString *, NSString * _Nullable, NSString * _Nullable, ActualSyncLong * _Nullable, NSString * _Nullable, NSString * _Nullable, ActualSyncLong * _Nullable, ActualSyncDouble * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable))mapper __attribute__((swift_name("getTransactionsByAccount(acct:mapper:)")));
- (ActualSyncRuntimeQuery<ActualSyncTransactions *> *)getTransactionsByDateRangeDate:(ActualSyncLong * _Nullable)date date_:(ActualSyncLong * _Nullable)date_ __attribute__((swift_name("getTransactionsByDateRange(date:date_:)")));
- (ActualSyncRuntimeQuery<id> *)getTransactionsByDateRangeDate:(ActualSyncLong * _Nullable)date date_:(ActualSyncLong * _Nullable)date_ mapper:(id (^)(NSString *, NSString * _Nullable, NSString * _Nullable, ActualSyncLong * _Nullable, NSString * _Nullable, NSString * _Nullable, ActualSyncLong * _Nullable, ActualSyncDouble * _Nullable, ActualSyncLong * _Nullable, ActualSyncLong * _Nullable))mapper __attribute__((swift_name("getTransactionsByDateRange(date:date_:mapper:)")));
- (void)insertAccountId:(NSString *)id name:(NSString * _Nullable)name offbudget:(ActualSyncLong * _Nullable)offbudget closed:(ActualSyncLong * _Nullable)closed sort_order:(ActualSyncDouble * _Nullable)sort_order tombstone:(ActualSyncLong * _Nullable)tombstone __attribute__((swift_name("insertAccount(id:name:offbudget:closed:sort_order:tombstone:)")));
- (void)insertBudgetId:(NSString *)id month:(int64_t)month category:(NSString *)category amount:(int64_t)amount carryover:(int64_t)carryover goal:(ActualSyncLong * _Nullable)goal tombstone:(int64_t)tombstone __attribute__((swift_name("insertBudget(id:month:category:amount:carryover:goal:tombstone:)")));
- (void)insertCategoryId:(NSString *)id name:(NSString * _Nullable)name cat_group:(NSString * _Nullable)cat_group is_income:(ActualSyncLong * _Nullable)is_income sort_order:(ActualSyncDouble * _Nullable)sort_order hidden:(ActualSyncLong * _Nullable)hidden tombstone:(ActualSyncLong * _Nullable)tombstone __attribute__((swift_name("insertCategory(id:name:cat_group:is_income:sort_order:hidden:tombstone:)")));
- (void)insertCategoryGroupId:(NSString *)id name:(NSString * _Nullable)name is_income:(ActualSyncLong * _Nullable)is_income sort_order:(ActualSyncDouble * _Nullable)sort_order hidden:(ActualSyncLong * _Nullable)hidden tombstone:(ActualSyncLong * _Nullable)tombstone __attribute__((swift_name("insertCategoryGroup(id:name:is_income:sort_order:hidden:tombstone:)")));
- (void)insertMessageTimestamp:(NSString *)timestamp dataset:(NSString *)dataset row:(NSString *)row column:(NSString *)column value_:(ActualSyncKotlinByteArray *)value_ __attribute__((swift_name("insertMessage(timestamp:dataset:row:column:value_:)")));
- (void)insertPayeeId:(NSString *)id name:(NSString * _Nullable)name category:(NSString * _Nullable)category tombstone:(ActualSyncLong * _Nullable)tombstone __attribute__((swift_name("insertPayee(id:name:category:tombstone:)")));
- (void)insertPayeeMappingId:(NSString *)id targetId:(NSString * _Nullable)targetId __attribute__((swift_name("insertPayeeMapping(id:targetId:)")));
- (void)insertTransactionId:(NSString *)id acct:(NSString * _Nullable)acct category:(NSString * _Nullable)category amount:(ActualSyncLong * _Nullable)amount description:(NSString * _Nullable)description notes:(NSString * _Nullable)notes date:(ActualSyncLong * _Nullable)date sort_order:(ActualSyncDouble * _Nullable)sort_order tombstone:(ActualSyncLong * _Nullable)tombstone cleared:(ActualSyncLong * _Nullable)cleared __attribute__((swift_name("insertTransaction(id:acct:category:amount:description:notes:date:sort_order:tombstone:cleared:)")));
- (ActualSyncRuntimeQuery<ActualSyncLong *> *)messageExistsTimestamp:(NSString *)timestamp __attribute__((swift_name("messageExists(timestamp:)")));
- (void)setSyncMetadataKey:(NSString *)key value_:(NSString * _Nullable)value_ __attribute__((swift_name("setSyncMetadata(key:value_:)")));
- (void)updateAccountColumnValue:(NSString *)value id:(NSString *)id name:(NSString * _Nullable)name __attribute__((swift_name("updateAccountColumn(value:id:name:)")));
- (void)updateCategoryColumnValue:(NSString *)value id:(NSString *)id name:(NSString * _Nullable)name __attribute__((swift_name("updateCategoryColumn(value:id:name:)")));
- (void)updateCategoryGroupColumnValue:(NSString *)value id:(NSString *)id name:(NSString * _Nullable)name __attribute__((swift_name("updateCategoryGroupColumn(value:id:name:)")));
- (void)updatePayeeColumnValue:(NSString *)value id:(NSString *)id name:(NSString * _Nullable)name __attribute__((swift_name("updatePayeeColumn(value:id:name:)")));
- (void)updateTransactionColumnValue:(NSString *)value id:(NSString *)id acct:(NSString * _Nullable)acct __attribute__((swift_name("updateTransactionColumn(value:id:acct:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Categories")))
@interface ActualSyncCategories : ActualSyncBase
- (instancetype)initWithId:(NSString *)id name:(NSString * _Nullable)name is_income:(ActualSyncLong * _Nullable)is_income cat_group:(NSString * _Nullable)cat_group sort_order:(ActualSyncDouble * _Nullable)sort_order tombstone:(ActualSyncLong * _Nullable)tombstone hidden:(ActualSyncLong * _Nullable)hidden __attribute__((swift_name("init(id:name:is_income:cat_group:sort_order:tombstone:hidden:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncCategories *)doCopyId:(NSString *)id name:(NSString * _Nullable)name is_income:(ActualSyncLong * _Nullable)is_income cat_group:(NSString * _Nullable)cat_group sort_order:(ActualSyncDouble * _Nullable)sort_order tombstone:(ActualSyncLong * _Nullable)tombstone hidden:(ActualSyncLong * _Nullable)hidden __attribute__((swift_name("doCopy(id:name:is_income:cat_group:sort_order:tombstone:hidden:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString * _Nullable cat_group __attribute__((swift_name("cat_group")));
@property (readonly) ActualSyncLong * _Nullable hidden __attribute__((swift_name("hidden")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) ActualSyncLong * _Nullable is_income __attribute__((swift_name("is_income")));
@property (readonly) NSString * _Nullable name __attribute__((swift_name("name")));
@property (readonly) ActualSyncDouble * _Nullable sort_order __attribute__((swift_name("sort_order")));
@property (readonly) ActualSyncLong * _Nullable tombstone __attribute__((swift_name("tombstone")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Category_groups")))
@interface ActualSyncCategory_groups : ActualSyncBase
- (instancetype)initWithId:(NSString *)id name:(NSString * _Nullable)name is_income:(ActualSyncLong * _Nullable)is_income sort_order:(ActualSyncDouble * _Nullable)sort_order tombstone:(ActualSyncLong * _Nullable)tombstone hidden:(ActualSyncLong * _Nullable)hidden __attribute__((swift_name("init(id:name:is_income:sort_order:tombstone:hidden:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncCategory_groups *)doCopyId:(NSString *)id name:(NSString * _Nullable)name is_income:(ActualSyncLong * _Nullable)is_income sort_order:(ActualSyncDouble * _Nullable)sort_order tombstone:(ActualSyncLong * _Nullable)tombstone hidden:(ActualSyncLong * _Nullable)hidden __attribute__((swift_name("doCopy(id:name:is_income:sort_order:tombstone:hidden:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncLong * _Nullable hidden __attribute__((swift_name("hidden")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) ActualSyncLong * _Nullable is_income __attribute__((swift_name("is_income")));
@property (readonly) NSString * _Nullable name __attribute__((swift_name("name")));
@property (readonly) ActualSyncDouble * _Nullable sort_order __attribute__((swift_name("sort_order")));
@property (readonly) ActualSyncLong * _Nullable tombstone __attribute__((swift_name("tombstone")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("DatabaseDriverFactory")))
@interface ActualSyncDatabaseDriverFactory : ActualSyncBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (id<ActualSyncRuntimeSqlDriver>)createDriverDbName:(NSString *)dbName __attribute__((swift_name("createDriver(dbName:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("GetLastTimestamp")))
@interface ActualSyncGetLastTimestamp : ActualSyncBase
- (instancetype)initWithLast_ts:(NSString * _Nullable)last_ts __attribute__((swift_name("init(last_ts:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncGetLastTimestamp *)doCopyLast_ts:(NSString * _Nullable)last_ts __attribute__((swift_name("doCopy(last_ts:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString * _Nullable last_ts __attribute__((swift_name("last_ts")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("GetSyncMetadata")))
@interface ActualSyncGetSyncMetadata : ActualSyncBase
- (instancetype)initWithValue_:(NSString * _Nullable)value_ __attribute__((swift_name("init(value_:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncGetSyncMetadata *)doCopyValue_:(NSString * _Nullable)value_ __attribute__((swift_name("doCopy(value_:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString * _Nullable value_ __attribute__((swift_name("value_")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Messages_crdt")))
@interface ActualSyncMessages_crdt : ActualSyncBase
- (instancetype)initWithId:(int64_t)id timestamp:(NSString *)timestamp dataset:(NSString *)dataset row:(NSString *)row column:(NSString *)column value_:(ActualSyncKotlinByteArray *)value_ __attribute__((swift_name("init(id:timestamp:dataset:row:column:value_:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncMessages_crdt *)doCopyId:(int64_t)id timestamp:(NSString *)timestamp dataset:(NSString *)dataset row:(NSString *)row column:(NSString *)column value_:(ActualSyncKotlinByteArray *)value_ __attribute__((swift_name("doCopy(id:timestamp:dataset:row:column:value_:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *column __attribute__((swift_name("column")));
@property (readonly) NSString *dataset __attribute__((swift_name("dataset")));
@property (readonly) int64_t id __attribute__((swift_name("id")));
@property (readonly) NSString *row __attribute__((swift_name("row")));
@property (readonly) NSString *timestamp __attribute__((swift_name("timestamp")));
@property (readonly) ActualSyncKotlinByteArray *value_ __attribute__((swift_name("value_")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Notes")))
@interface ActualSyncNotes : ActualSyncBase
- (instancetype)initWithId:(NSString *)id note:(NSString *)note __attribute__((swift_name("init(id:note:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncNotes *)doCopyId:(NSString *)id note:(NSString *)note __attribute__((swift_name("doCopy(id:note:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) NSString *note __attribute__((swift_name("note")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Payee_mapping")))
@interface ActualSyncPayee_mapping : ActualSyncBase
- (instancetype)initWithId:(NSString *)id targetId:(NSString * _Nullable)targetId __attribute__((swift_name("init(id:targetId:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncPayee_mapping *)doCopyId:(NSString *)id targetId:(NSString * _Nullable)targetId __attribute__((swift_name("doCopy(id:targetId:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) NSString * _Nullable targetId __attribute__((swift_name("targetId")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Payees")))
@interface ActualSyncPayees : ActualSyncBase
- (instancetype)initWithId:(NSString *)id name:(NSString * _Nullable)name category:(NSString * _Nullable)category tombstone:(ActualSyncLong * _Nullable)tombstone __attribute__((swift_name("init(id:name:category:tombstone:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncPayees *)doCopyId:(NSString *)id name:(NSString * _Nullable)name category:(NSString * _Nullable)category tombstone:(ActualSyncLong * _Nullable)tombstone __attribute__((swift_name("doCopy(id:name:category:tombstone:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString * _Nullable category __attribute__((swift_name("category")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) NSString * _Nullable name __attribute__((swift_name("name")));
@property (readonly) ActualSyncLong * _Nullable tombstone __attribute__((swift_name("tombstone")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Rules")))
@interface ActualSyncRules : ActualSyncBase
- (instancetype)initWithId:(NSString *)id stage:(NSString * _Nullable)stage conditions:(NSString * _Nullable)conditions actions:(NSString * _Nullable)actions tombstone:(int64_t)tombstone __attribute__((swift_name("init(id:stage:conditions:actions:tombstone:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncRules *)doCopyId:(NSString *)id stage:(NSString * _Nullable)stage conditions:(NSString * _Nullable)conditions actions:(NSString * _Nullable)actions tombstone:(int64_t)tombstone __attribute__((swift_name("doCopy(id:stage:conditions:actions:tombstone:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString * _Nullable actions __attribute__((swift_name("actions")));
@property (readonly) NSString * _Nullable conditions __attribute__((swift_name("conditions")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) NSString * _Nullable stage __attribute__((swift_name("stage")));
@property (readonly) int64_t tombstone __attribute__((swift_name("tombstone")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Schedule_next_date")))
@interface ActualSyncSchedule_next_date : ActualSyncBase
- (instancetype)initWithId:(NSString *)id schedule_id:(NSString *)schedule_id local_next_date:(ActualSyncLong * _Nullable)local_next_date local_next_date_ts:(ActualSyncLong * _Nullable)local_next_date_ts base_next_date:(ActualSyncLong * _Nullable)base_next_date base_next_date_ts:(ActualSyncLong * _Nullable)base_next_date_ts tombstone:(int64_t)tombstone __attribute__((swift_name("init(id:schedule_id:local_next_date:local_next_date_ts:base_next_date:base_next_date_ts:tombstone:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncSchedule_next_date *)doCopyId:(NSString *)id schedule_id:(NSString *)schedule_id local_next_date:(ActualSyncLong * _Nullable)local_next_date local_next_date_ts:(ActualSyncLong * _Nullable)local_next_date_ts base_next_date:(ActualSyncLong * _Nullable)base_next_date base_next_date_ts:(ActualSyncLong * _Nullable)base_next_date_ts tombstone:(int64_t)tombstone __attribute__((swift_name("doCopy(id:schedule_id:local_next_date:local_next_date_ts:base_next_date:base_next_date_ts:tombstone:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncLong * _Nullable base_next_date __attribute__((swift_name("base_next_date")));
@property (readonly) ActualSyncLong * _Nullable base_next_date_ts __attribute__((swift_name("base_next_date_ts")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) ActualSyncLong * _Nullable local_next_date __attribute__((swift_name("local_next_date")));
@property (readonly) ActualSyncLong * _Nullable local_next_date_ts __attribute__((swift_name("local_next_date_ts")));
@property (readonly) NSString *schedule_id __attribute__((swift_name("schedule_id")));
@property (readonly) int64_t tombstone __attribute__((swift_name("tombstone")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Schedules")))
@interface ActualSyncSchedules : ActualSyncBase
- (instancetype)initWithId:(NSString *)id rule:(NSString * _Nullable)rule active:(int64_t)active completed:(int64_t)completed posts_transaction:(int64_t)posts_transaction tombstone:(int64_t)tombstone name:(NSString * _Nullable)name __attribute__((swift_name("init(id:rule:active:completed:posts_transaction:tombstone:name:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncSchedules *)doCopyId:(NSString *)id rule:(NSString * _Nullable)rule active:(int64_t)active completed:(int64_t)completed posts_transaction:(int64_t)posts_transaction tombstone:(int64_t)tombstone name:(NSString * _Nullable)name __attribute__((swift_name("doCopy(id:rule:active:completed:posts_transaction:tombstone:name:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int64_t active __attribute__((swift_name("active")));
@property (readonly) int64_t completed __attribute__((swift_name("completed")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) NSString * _Nullable name __attribute__((swift_name("name")));
@property (readonly) int64_t posts_transaction __attribute__((swift_name("posts_transaction")));
@property (readonly) NSString * _Nullable rule __attribute__((swift_name("rule")));
@property (readonly) int64_t tombstone __attribute__((swift_name("tombstone")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SyncRepository")))
@interface ActualSyncSyncRepository : ActualSyncBase
- (instancetype)initWithDb:(id<ActualSyncActualDatabase>)db __attribute__((swift_name("init(db:)"))) __attribute__((objc_designated_initializer));
- (void)applyMessagesEnvelopes:(NSArray<ActualSyncMessageEnvelope *> *)envelopes __attribute__((swift_name("applyMessages(envelopes:)")));
- (void)clearAll __attribute__((swift_name("clearAll()")));
- (NSArray<ActualSyncAccounts *> *)getAccounts __attribute__((swift_name("getAccounts()")));
- (NSArray<ActualSyncZero_budgets *> *)getBudgetForCategoryCategory:(NSString *)category __attribute__((swift_name("getBudgetForCategory(category:)")));
- (NSArray<ActualSyncZero_budgets *> *)getBudgetForMonthMonth:(int64_t)month __attribute__((swift_name("getBudgetForMonth(month:)")));
- (NSArray<ActualSyncCategories *> *)getCategories __attribute__((swift_name("getCategories()")));
- (NSArray<ActualSyncCategory_groups *> *)getCategoryGroups __attribute__((swift_name("getCategoryGroups()")));
- (NSString * _Nullable)getLastSyncTimestamp __attribute__((swift_name("getLastSyncTimestamp()")));
- (NSArray<ActualSyncPayees *> *)getPayees __attribute__((swift_name("getPayees()")));
- (NSString * _Nullable)getSyncMetadataKey:(NSString *)key __attribute__((swift_name("getSyncMetadata(key:)")));
- (NSArray<ActualSyncTransactions *> *)getTransactionsByAccountAccountId:(NSString *)accountId __attribute__((swift_name("getTransactionsByAccount(accountId:)")));
- (NSArray<ActualSyncTransactions *> *)getTransactionsByDateRangeStartDate:(int64_t)startDate endDate:(int64_t)endDate __attribute__((swift_name("getTransactionsByDateRange(startDate:endDate:)")));
- (void)setSyncMetadataKey:(NSString *)key value:(NSString *)value __attribute__((swift_name("setSyncMetadata(key:value:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Sync_metadata")))
@interface ActualSyncSync_metadata : ActualSyncBase
- (instancetype)initWithKey:(NSString *)key value_:(NSString * _Nullable)value_ __attribute__((swift_name("init(key:value_:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncSync_metadata *)doCopyKey:(NSString *)key value_:(NSString * _Nullable)value_ __attribute__((swift_name("doCopy(key:value_:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *key __attribute__((swift_name("key")));
@property (readonly) NSString * _Nullable value_ __attribute__((swift_name("value_")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Transactions")))
@interface ActualSyncTransactions : ActualSyncBase
- (instancetype)initWithId:(NSString *)id acct:(NSString * _Nullable)acct category:(NSString * _Nullable)category amount:(ActualSyncLong * _Nullable)amount description:(NSString * _Nullable)description notes:(NSString * _Nullable)notes date:(ActualSyncLong * _Nullable)date sort_order:(ActualSyncDouble * _Nullable)sort_order tombstone:(ActualSyncLong * _Nullable)tombstone cleared:(ActualSyncLong * _Nullable)cleared __attribute__((swift_name("init(id:acct:category:amount:description:notes:date:sort_order:tombstone:cleared:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncTransactions *)doCopyId:(NSString *)id acct:(NSString * _Nullable)acct category:(NSString * _Nullable)category amount:(ActualSyncLong * _Nullable)amount description:(NSString * _Nullable)description notes:(NSString * _Nullable)notes date:(ActualSyncLong * _Nullable)date sort_order:(ActualSyncDouble * _Nullable)sort_order tombstone:(ActualSyncLong * _Nullable)tombstone cleared:(ActualSyncLong * _Nullable)cleared __attribute__((swift_name("doCopy(id:acct:category:amount:description:notes:date:sort_order:tombstone:cleared:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString * _Nullable acct __attribute__((swift_name("acct")));
@property (readonly) ActualSyncLong * _Nullable amount __attribute__((swift_name("amount")));
@property (readonly) NSString * _Nullable category __attribute__((swift_name("category")));
@property (readonly) ActualSyncLong * _Nullable cleared __attribute__((swift_name("cleared")));
@property (readonly) ActualSyncLong * _Nullable date __attribute__((swift_name("date")));
@property (readonly) NSString * _Nullable description_ __attribute__((swift_name("description_")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) NSString * _Nullable notes __attribute__((swift_name("notes")));
@property (readonly) ActualSyncDouble * _Nullable sort_order __attribute__((swift_name("sort_order")));
@property (readonly) ActualSyncLong * _Nullable tombstone __attribute__((swift_name("tombstone")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Zero_budgets")))
@interface ActualSyncZero_budgets : ActualSyncBase
- (instancetype)initWithId:(NSString *)id month:(int64_t)month category:(NSString *)category amount:(int64_t)amount carryover:(int64_t)carryover goal:(ActualSyncLong * _Nullable)goal tombstone:(int64_t)tombstone __attribute__((swift_name("init(id:month:category:amount:carryover:goal:tombstone:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncZero_budgets *)doCopyId:(NSString *)id month:(int64_t)month category:(NSString *)category amount:(int64_t)amount carryover:(int64_t)carryover goal:(ActualSyncLong * _Nullable)goal tombstone:(int64_t)tombstone __attribute__((swift_name("doCopy(id:month:category:amount:carryover:goal:tombstone:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int64_t amount __attribute__((swift_name("amount")));
@property (readonly) int64_t carryover __attribute__((swift_name("carryover")));
@property (readonly) NSString *category __attribute__((swift_name("category")));
@property (readonly) ActualSyncLong * _Nullable goal __attribute__((swift_name("goal")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) int64_t month __attribute__((swift_name("month")));
@property (readonly) int64_t tombstone __attribute__((swift_name("tombstone")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BudgetFileManager")))
@interface ActualSyncBudgetFileManager : ActualSyncBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (BOOL)doCopySource:(NSString *)source destination:(NSString *)destination __attribute__((swift_name("doCopy(source:destination:)")));
- (BOOL)deletePath:(NSString *)path __attribute__((swift_name("delete(path:)")));
- (BOOL)existsPath:(NSString *)path __attribute__((swift_name("exists(path:)")));
- (NSString * _Nullable)extractBudgetZipZipData:(ActualSyncKotlinByteArray *)zipData targetDir:(NSString *)targetDir __attribute__((swift_name("extractBudgetZip(zipData:targetDir:)")));
- (NSString *)getDefaultBudgetDir __attribute__((swift_name("getDefaultBudgetDir()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("EncryptedData")))
@interface ActualSyncEncryptedData : ActualSyncBase
- (instancetype)initWithIv:(ActualSyncKotlinByteArray *)iv authTag:(ActualSyncKotlinByteArray *)authTag data:(ActualSyncKotlinByteArray *)data __attribute__((swift_name("init(iv:authTag:data:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncEncryptedDataCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncEncryptedData *)doCopyIv:(ActualSyncKotlinByteArray *)iv authTag:(ActualSyncKotlinByteArray *)authTag data:(ActualSyncKotlinByteArray *)data __attribute__((swift_name("doCopy(iv:authTag:data:)")));
- (ActualSyncKotlinByteArray *)encode __attribute__((swift_name("encode()")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncKotlinByteArray *authTag __attribute__((swift_name("authTag")));
@property (readonly) ActualSyncKotlinByteArray *data __attribute__((swift_name("data")));
@property (readonly) ActualSyncKotlinByteArray *iv __attribute__((swift_name("iv")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("EncryptedData.Companion")))
@interface ActualSyncEncryptedDataCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncEncryptedDataCompanion *shared __attribute__((swift_name("shared")));
- (ActualSyncEncryptedData *)decodeData:(ActualSyncKotlinByteArray *)data __attribute__((swift_name("decode(data:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Message")))
@interface ActualSyncMessage : ActualSyncBase
- (instancetype)initWithDataset:(NSString *)dataset row:(NSString *)row column:(NSString *)column value:(NSString *)value __attribute__((swift_name("init(dataset:row:column:value:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncMessageCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncMessage *)doCopyDataset:(NSString *)dataset row:(NSString *)row column:(NSString *)column value:(NSString *)value __attribute__((swift_name("doCopy(dataset:row:column:value:)")));
- (ActualSyncKotlinByteArray *)encode __attribute__((swift_name("encode()")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *column __attribute__((swift_name("column")));
@property (readonly) NSString *dataset __attribute__((swift_name("dataset")));
@property (readonly) NSString *row __attribute__((swift_name("row")));
@property (readonly) NSString *value __attribute__((swift_name("value")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Message.Companion")))
@interface ActualSyncMessageCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncMessageCompanion *shared __attribute__((swift_name("shared")));
- (ActualSyncMessage *)decodeData:(ActualSyncKotlinByteArray *)data __attribute__((swift_name("decode(data:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MessageEnvelope")))
@interface ActualSyncMessageEnvelope : ActualSyncBase
- (instancetype)initWithTimestamp:(NSString *)timestamp isEncrypted:(BOOL)isEncrypted content:(ActualSyncKotlinByteArray *)content __attribute__((swift_name("init(timestamp:isEncrypted:content:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncMessageEnvelopeCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncMessageEnvelope *)doCopyTimestamp:(NSString *)timestamp isEncrypted:(BOOL)isEncrypted content:(ActualSyncKotlinByteArray *)content __attribute__((swift_name("doCopy(timestamp:isEncrypted:content:)")));
- (ActualSyncMessage *)decodeMessage __attribute__((swift_name("decodeMessage()")));
- (ActualSyncKotlinByteArray *)encode __attribute__((swift_name("encode()")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncKotlinByteArray *content __attribute__((swift_name("content")));
@property (readonly) BOOL isEncrypted __attribute__((swift_name("isEncrypted")));
@property (readonly) NSString *timestamp __attribute__((swift_name("timestamp")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MessageEnvelope.Companion")))
@interface ActualSyncMessageEnvelopeCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncMessageEnvelopeCompanion *shared __attribute__((swift_name("shared")));
- (ActualSyncMessageEnvelope *)createTimestamp:(NSString *)timestamp message:(ActualSyncMessage *)message __attribute__((swift_name("create(timestamp:message:)")));
- (ActualSyncMessageEnvelope *)decodeData:(ActualSyncKotlinByteArray *)data __attribute__((swift_name("decode(data:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Protobuf")))
@interface ActualSyncProtobuf : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)protobuf __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncProtobuf *shared __attribute__((swift_name("shared")));
- (ActualSyncKotlinByteArray *)encodeBoolFieldNumber:(int32_t)fieldNumber value:(BOOL)value __attribute__((swift_name("encodeBool(fieldNumber:value:)")));
- (ActualSyncKotlinByteArray *)encodeBytesFieldNumber:(int32_t)fieldNumber value:(ActualSyncKotlinByteArray *)value __attribute__((swift_name("encodeBytes(fieldNumber:value:)")));
- (ActualSyncKotlinByteArray *)encodeMessageFieldNumber:(int32_t)fieldNumber value:(ActualSyncKotlinByteArray *)value __attribute__((swift_name("encodeMessage(fieldNumber:value:)")));
- (ActualSyncKotlinByteArray *)encodeStringFieldNumber:(int32_t)fieldNumber value:(NSString *)value __attribute__((swift_name("encodeString(fieldNumber:value:)")));
- (ActualSyncKotlinByteArray *)encodeTagFieldNumber:(int32_t)fieldNumber wireType:(int32_t)wireType __attribute__((swift_name("encodeTag(fieldNumber:wireType:)")));
- (ActualSyncKotlinByteArray *)encodeVarintValue:(int32_t)value __attribute__((swift_name("encodeVarint(value:)")));
- (ActualSyncKotlinByteArray *)encodeVarintValue_:(int64_t)value __attribute__((swift_name("encodeVarint(value_:)")));
@property (readonly) int32_t WIRE_LENGTH_DELIMITED __attribute__((swift_name("WIRE_LENGTH_DELIMITED")));
@property (readonly) int32_t WIRE_VARINT __attribute__((swift_name("WIRE_VARINT")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ProtobufReader")))
@interface ActualSyncProtobufReader : ActualSyncBase
- (instancetype)initWithData:(ActualSyncKotlinByteArray *)data __attribute__((swift_name("init(data:)"))) __attribute__((objc_designated_initializer));
- (BOOL)readBool __attribute__((swift_name("readBool()")));
- (ActualSyncKotlinByteArray *)readBytes __attribute__((swift_name("readBytes()")));
- (NSString *)readString __attribute__((swift_name("readString()")));
- (ActualSyncKotlinPair<ActualSyncInt *, ActualSyncInt *> *)readTag __attribute__((swift_name("readTag()")));
- (int64_t)readVarint __attribute__((swift_name("readVarint()")));
- (void)skipFieldWireType:(int32_t)wireType __attribute__((swift_name("skipField(wireType:)")));
@property (readonly) BOOL isAtEnd __attribute__((swift_name("isAtEnd")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ProtobufWriter")))
@interface ActualSyncProtobufWriter : ActualSyncBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (ActualSyncKotlinByteArray *)toByteArray __attribute__((swift_name("toByteArray()")));
- (void)writeBoolFieldNumber:(int32_t)fieldNumber value:(BOOL)value __attribute__((swift_name("writeBool(fieldNumber:value:)")));
- (void)writeBytesFieldNumber:(int32_t)fieldNumber value:(ActualSyncKotlinByteArray *)value __attribute__((swift_name("writeBytes(fieldNumber:value:)")));
- (void)writeMessageFieldNumber:(int32_t)fieldNumber value:(ActualSyncKotlinByteArray *)value __attribute__((swift_name("writeMessage(fieldNumber:value:)")));
- (void)writeStringFieldNumber:(int32_t)fieldNumber value:(NSString *)value __attribute__((swift_name("writeString(fieldNumber:value:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SyncRequest")))
@interface ActualSyncSyncRequest : ActualSyncBase
- (instancetype)initWithMessages:(NSArray<ActualSyncMessageEnvelope *> *)messages fileId:(NSString *)fileId groupId:(NSString *)groupId keyId:(NSString *)keyId since:(NSString *)since __attribute__((swift_name("init(messages:fileId:groupId:keyId:since:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncSyncRequestCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncSyncRequest *)doCopyMessages:(NSArray<ActualSyncMessageEnvelope *> *)messages fileId:(NSString *)fileId groupId:(NSString *)groupId keyId:(NSString *)keyId since:(NSString *)since __attribute__((swift_name("doCopy(messages:fileId:groupId:keyId:since:)")));
- (ActualSyncKotlinByteArray *)encode __attribute__((swift_name("encode()")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *fileId __attribute__((swift_name("fileId")));
@property (readonly) NSString *groupId __attribute__((swift_name("groupId")));
@property (readonly) NSString *keyId __attribute__((swift_name("keyId")));
@property (readonly) NSArray<ActualSyncMessageEnvelope *> *messages __attribute__((swift_name("messages")));
@property (readonly) NSString *since __attribute__((swift_name("since")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SyncRequest.Companion")))
@interface ActualSyncSyncRequestCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncSyncRequestCompanion *shared __attribute__((swift_name("shared")));
- (ActualSyncSyncRequest *)decodeData:(ActualSyncKotlinByteArray *)data __attribute__((swift_name("decode(data:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SyncResponse")))
@interface ActualSyncSyncResponse : ActualSyncBase
- (instancetype)initWithMessages:(NSArray<ActualSyncMessageEnvelope *> *)messages merkle:(NSString *)merkle __attribute__((swift_name("init(messages:merkle:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncSyncResponseCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncSyncResponse *)doCopyMessages:(NSArray<ActualSyncMessageEnvelope *> *)messages merkle:(NSString *)merkle __attribute__((swift_name("doCopy(messages:merkle:)")));
- (ActualSyncKotlinByteArray *)encode __attribute__((swift_name("encode()")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *merkle __attribute__((swift_name("merkle")));
@property (readonly) NSArray<ActualSyncMessageEnvelope *> *messages __attribute__((swift_name("messages")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SyncResponse.Companion")))
@interface ActualSyncSyncResponseCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncSyncResponseCompanion *shared __attribute__((swift_name("shared")));
- (ActualSyncSyncResponse *)decodeData:(ActualSyncKotlinByteArray *)data __attribute__((swift_name("decode(data:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BudgetFile")))
@interface ActualSyncBudgetFile : ActualSyncBase
- (instancetype)initWithId:(NSString *)id name:(NSString *)name groupId:(NSString * _Nullable)groupId encryptKeyId:(NSString * _Nullable)encryptKeyId deleted:(BOOL)deleted __attribute__((swift_name("init(id:name:groupId:encryptKeyId:deleted:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncBudgetFile *)doCopyId:(NSString *)id name:(NSString *)name groupId:(NSString * _Nullable)groupId encryptKeyId:(NSString * _Nullable)encryptKeyId deleted:(BOOL)deleted __attribute__((swift_name("doCopy(id:name:groupId:encryptKeyId:deleted:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) BOOL deleted __attribute__((swift_name("deleted")));
@property (readonly) NSString * _Nullable encryptKeyId __attribute__((swift_name("encryptKeyId")));
@property (readonly) NSString * _Nullable groupId __attribute__((swift_name("groupId")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@end


/**
 * @note annotations
 *   kotlinx.serialization.Serializable
*/
__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FileData")))
@interface ActualSyncFileData : ActualSyncBase
- (instancetype)initWithId:(NSString * _Nullable)id fileId:(NSString * _Nullable)fileId name:(NSString * _Nullable)name groupId:(NSString * _Nullable)groupId encryptKeyId:(NSString * _Nullable)encryptKeyId deleted:(ActualSyncInt * _Nullable)deleted __attribute__((swift_name("init(id:fileId:name:groupId:encryptKeyId:deleted:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncFileDataCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncFileData *)doCopyId:(NSString * _Nullable)id fileId:(NSString * _Nullable)fileId name:(NSString * _Nullable)name groupId:(NSString * _Nullable)groupId encryptKeyId:(NSString * _Nullable)encryptKeyId deleted:(ActualSyncInt * _Nullable)deleted __attribute__((swift_name("doCopy(id:fileId:name:groupId:encryptKeyId:deleted:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncInt * _Nullable deleted __attribute__((swift_name("deleted")));
@property (readonly) NSString * _Nullable encryptKeyId __attribute__((swift_name("encryptKeyId")));
@property (readonly) NSString * _Nullable fileId __attribute__((swift_name("fileId")));
@property (readonly) NSString * _Nullable groupId __attribute__((swift_name("groupId")));
@property (readonly) NSString * _Nullable id __attribute__((swift_name("id")));
@property (readonly) NSString * _Nullable name __attribute__((swift_name("name")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FileData.Companion")))
@interface ActualSyncFileDataCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncFileDataCompanion *shared __attribute__((swift_name("shared")));
- (id<ActualSyncKotlinx_serialization_coreKSerializer>)serializer __attribute__((swift_name("serializer()")));
@end


/**
 * @note annotations
 *   kotlinx.serialization.Serializable
*/
__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ListFilesResponse")))
@interface ActualSyncListFilesResponse : ActualSyncBase
- (instancetype)initWithStatus:(NSString * _Nullable)status data:(NSArray<ActualSyncFileData *> * _Nullable)data __attribute__((swift_name("init(status:data:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncListFilesResponseCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncListFilesResponse *)doCopyStatus:(NSString * _Nullable)status data:(NSArray<ActualSyncFileData *> * _Nullable)data __attribute__((swift_name("doCopy(status:data:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSArray<ActualSyncFileData *> * _Nullable data __attribute__((swift_name("data")));
@property (readonly) NSString * _Nullable status __attribute__((swift_name("status")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ListFilesResponse.Companion")))
@interface ActualSyncListFilesResponseCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncListFilesResponseCompanion *shared __attribute__((swift_name("shared")));
- (id<ActualSyncKotlinx_serialization_coreKSerializer>)serializer __attribute__((swift_name("serializer()")));
@end


/**
 * @note annotations
 *   kotlinx.serialization.Serializable
*/
__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("LoginData")))
@interface ActualSyncLoginData : ActualSyncBase
- (instancetype)initWithToken:(NSString * _Nullable)token __attribute__((swift_name("init(token:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncLoginDataCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncLoginData *)doCopyToken:(NSString * _Nullable)token __attribute__((swift_name("doCopy(token:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString * _Nullable token __attribute__((swift_name("token")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("LoginData.Companion")))
@interface ActualSyncLoginDataCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncLoginDataCompanion *shared __attribute__((swift_name("shared")));
- (id<ActualSyncKotlinx_serialization_coreKSerializer>)serializer __attribute__((swift_name("serializer()")));
@end


/**
 * @note annotations
 *   kotlinx.serialization.Serializable
*/
__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("LoginResponse")))
@interface ActualSyncLoginResponse : ActualSyncBase
- (instancetype)initWithStatus:(NSString * _Nullable)status data:(ActualSyncLoginData * _Nullable)data __attribute__((swift_name("init(status:data:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncLoginResponseCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncLoginResponse *)doCopyStatus:(NSString * _Nullable)status data:(ActualSyncLoginData * _Nullable)data __attribute__((swift_name("doCopy(status:data:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncLoginData * _Nullable data __attribute__((swift_name("data")));
@property (readonly) NSString * _Nullable status __attribute__((swift_name("status")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("LoginResponse.Companion")))
@interface ActualSyncLoginResponseCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncLoginResponseCompanion *shared __attribute__((swift_name("shared")));
- (id<ActualSyncKotlinx_serialization_coreKSerializer>)serializer __attribute__((swift_name("serializer()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MessageEnvelope_")))
@interface ActualSyncMessageEnvelope_ : ActualSyncBase
- (instancetype)initWithTimestamp:(NSString *)timestamp isEncrypted:(BOOL)isEncrypted content:(ActualSyncKotlinByteArray *)content __attribute__((swift_name("init(timestamp:isEncrypted:content:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncMessageEnvelope_ *)doCopyTimestamp:(NSString *)timestamp isEncrypted:(BOOL)isEncrypted content:(ActualSyncKotlinByteArray *)content __attribute__((swift_name("doCopy(timestamp:isEncrypted:content:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncKotlinByteArray *content __attribute__((swift_name("content")));
@property (readonly) BOOL isEncrypted __attribute__((swift_name("isEncrypted")));
@property (readonly) NSString *timestamp __attribute__((swift_name("timestamp")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("PendingChangeDetail")))
@interface ActualSyncPendingChangeDetail : ActualSyncBase
- (instancetype)initWithDataset:(NSString *)dataset rowId:(NSString *)rowId column:(NSString *)column value:(NSString *)value timestamp:(NSString *)timestamp __attribute__((swift_name("init(dataset:rowId:column:value:timestamp:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncPendingChangeDetail *)doCopyDataset:(NSString *)dataset rowId:(NSString *)rowId column:(NSString *)column value:(NSString *)value timestamp:(NSString *)timestamp __attribute__((swift_name("doCopy(dataset:rowId:column:value:timestamp:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *column __attribute__((swift_name("column")));
@property (readonly) NSString *dataset __attribute__((swift_name("dataset")));
@property (readonly) NSString *rowId __attribute__((swift_name("rowId")));
@property (readonly) NSString *timestamp __attribute__((swift_name("timestamp")));
@property (readonly) NSString *value __attribute__((swift_name("value")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SyncClient")))
@interface ActualSyncSyncClient : ActualSyncBase
- (instancetype)initWithServerUrl:(NSString *)serverUrl httpClient:(ActualSyncKtor_client_coreHttpClient *)httpClient __attribute__((swift_name("init(serverUrl:httpClient:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of Exception to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)downloadBudgetSyncId:(NSString *)syncId completionHandler:(void (^)(ActualSyncKotlinByteArray * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("downloadBudget(syncId:completionHandler:)")));
- (NSString * _Nullable)getToken __attribute__((swift_name("getToken()")));

/**
 * @note This method converts instances of Exception to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)listFilesWithCompletionHandler:(void (^)(NSArray<ActualSyncBudgetFile *> * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("listFiles(completionHandler:)")));

/**
 * @note This method converts instances of Exception to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)loginPassword:(NSString *)password completionHandler:(void (^)(NSString * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("login(password:completionHandler:)")));
- (void)setTokenToken:(NSString *)token __attribute__((swift_name("setToken(token:)")));

/**
 * @note This method converts instances of Exception to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)syncMessages:(NSArray<ActualSyncMessageEnvelope_ *> *)messages since:(NSString *)since completionHandler:(void (^)(ActualSyncSyncResponse_ * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("sync(messages:since:completionHandler:)")));

/**
 * @note This method converts instances of Exception to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)uploadBudgetFileId:(NSString *)fileId name:(NSString *)name data:(ActualSyncKotlinByteArray *)data groupId:(NSString * _Nullable)groupId completionHandler:(void (^)(NSString * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("uploadBudget(fileId:name:data:groupId:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SyncEngine")))
@interface ActualSyncSyncEngine : ActualSyncBase
- (instancetype)initWithDb:(id<ActualSyncActualDatabase>)db clock:(ActualSyncMutableClock *)clock __attribute__((swift_name("init(db:clock:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncSyncRequest *)buildIncrementalSyncRequestFileId:(NSString *)fileId groupId:(NSString *)groupId serverMerkle:(ActualSyncTrieNode *)serverMerkle __attribute__((swift_name("buildIncrementalSyncRequest(fileId:groupId:serverMerkle:)")));
- (ActualSyncSyncRequest *)buildSyncRequestFileId:(NSString *)fileId groupId:(NSString *)groupId fullSync:(BOOL)fullSync __attribute__((swift_name("buildSyncRequest(fileId:groupId:fullSync:)")));
- (void)clearPendingMessages __attribute__((swift_name("clearPendingMessages()")));
- (ActualSyncMessageEnvelope *)createChangeDataset:(NSString *)dataset row:(NSString *)row column:(NSString *)column value:(id _Nullable)value __attribute__((swift_name("createChange(dataset:row:column:value:)")));
- (ActualSyncTrieNode *)getLocalMerkle __attribute__((swift_name("getLocalMerkle()")));
- (NSArray<ActualSyncMessageEnvelope *> *)getPendingMessages __attribute__((swift_name("getPendingMessages()")));
- (ActualSyncTrieNode * _Nullable)getServerMerkle __attribute__((swift_name("getServerMerkle()")));
- (void)initialize __attribute__((swift_name("initialize()")));
- (BOOL)isInSync __attribute__((swift_name("isInSync()")));
- (int32_t)processSyncResponseResponse:(ActualSyncSyncResponse *)response __attribute__((swift_name("processSyncResponse(response:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SyncManager")))
@interface ActualSyncSyncManager : ActualSyncBase
- (instancetype)initWithServerUrl:(NSString *)serverUrl httpClient:(ActualSyncKtor_client_coreHttpClient *)httpClient database:(id<ActualSyncActualDatabase>)database __attribute__((swift_name("init(serverUrl:httpClient:database:)"))) __attribute__((objc_designated_initializer));
- (NSString *)createAccountId:(NSString *)id name:(NSString *)name offbudget:(BOOL)offbudget __attribute__((swift_name("createAccount(id:name:offbudget:)")));
- (NSString *)createCategoryId:(NSString *)id name:(NSString *)name groupId:(NSString *)groupId __attribute__((swift_name("createCategory(id:name:groupId:)")));
- (NSString *)createPayeeId:(NSString *)id name:(NSString *)name __attribute__((swift_name("createPayee(id:name:)")));
- (NSString *)createTransactionId:(NSString *)id accountId:(NSString *)accountId date:(int32_t)date amount:(int64_t)amount payeeId:(NSString * _Nullable)payeeId categoryId:(NSString * _Nullable)categoryId notes:(NSString * _Nullable)notes __attribute__((swift_name("createTransaction(id:accountId:date:amount:payeeId:categoryId:notes:)")));
- (void)deleteAccountId:(NSString *)id __attribute__((swift_name("deleteAccount(id:)")));
- (void)deleteCategoryId:(NSString *)id __attribute__((swift_name("deleteCategory(id:)")));
- (void)deletePayeeId:(NSString *)id __attribute__((swift_name("deletePayee(id:)")));
- (void)deleteTransactionId:(NSString *)id __attribute__((swift_name("deleteTransaction(id:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)downloadAndInstallBudgetFileId:(NSString *)fileId targetDbPath:(NSString *)targetDbPath completionHandler:(void (^)(ActualSyncBoolean * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("downloadAndInstallBudget(fileId:targetDbPath:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)downloadBudgetFileFileId:(NSString *)fileId completionHandler:(void (^)(ActualSyncKotlinByteArray * _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("downloadBudgetFile(fileId:completionHandler:)")));
- (BOOL)extractAndInstallBudgetZipData:(ActualSyncKotlinByteArray *)zipData targetDbPath:(NSString *)targetDbPath __attribute__((swift_name("extractAndInstallBudget(zipData:targetDbPath:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)fullSyncWithCompletionHandler:(void (^)(ActualSyncSyncResult * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("fullSync(completionHandler:)")));

/**
 * @note This method converts instances of Exception to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (NSArray<ActualSyncAccounts *> * _Nullable)getAccountsSafeAndReturnError:(NSError * _Nullable * _Nullable)error __attribute__((swift_name("getAccountsSafe()")));

/**
 * @note This method converts instances of Exception to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (NSArray<ActualSyncZero_budgets *> * _Nullable)getBudgetForMonthSafeMonth:(int64_t)month error:(NSError * _Nullable * _Nullable)error __attribute__((swift_name("getBudgetForMonthSafe(month:)")));

/**
 * @note This method converts instances of Exception to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (NSArray<ActualSyncCategories *> * _Nullable)getCategoriesSafeAndReturnError:(NSError * _Nullable * _Nullable)error __attribute__((swift_name("getCategoriesSafe()")));

/**
 * @note This method converts instances of Exception to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (NSArray<ActualSyncCategory_groups *> * _Nullable)getCategoryGroupsSafeAndReturnError:(NSError * _Nullable * _Nullable)error __attribute__((swift_name("getCategoryGroupsSafe()")));

/**
 * @note This method converts instances of Exception to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (NSString * _Nullable)getDatabaseDiagnosticsAndReturnError:(NSError * _Nullable * _Nullable)error __attribute__((swift_name("getDatabaseDiagnostics()")));
- (ActualSyncSyncEngine *)getEngine __attribute__((swift_name("getEngine()")));

/**
 * @note This method converts instances of Exception to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (NSArray<ActualSyncPayees *> * _Nullable)getPayeesSafeAndReturnError:(NSError * _Nullable * _Nullable)error __attribute__((swift_name("getPayeesSafe()")));
- (int32_t)getPendingChangeCount __attribute__((swift_name("getPendingChangeCount()")));
- (NSArray<ActualSyncPendingChangeDetail *> *)getPendingChangeDetails __attribute__((swift_name("getPendingChangeDetails()")));
- (NSArray<NSString *> *)getPendingChangeSummary __attribute__((swift_name("getPendingChangeSummary()")));

/**
 * @note This method converts instances of Exception to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (NSArray<ActualSyncTransactions *> * _Nullable)getTransactionsByAccountSafeAccountId:(NSString *)accountId error:(NSError * _Nullable * _Nullable)error __attribute__((swift_name("getTransactionsByAccountSafe(accountId:)")));
- (void)initializeClientId:(NSString * _Nullable)clientId __attribute__((swift_name("initialize(clientId:)")));
- (BOOL)isInSync __attribute__((swift_name("isInSync()")));

/**
 * @note This method converts instances of Exception to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (NSString * _Nullable)rawQuerySql:(NSString *)sql error:(NSError * _Nullable * _Nullable)error __attribute__((swift_name("rawQuery(sql:)")));
- (void)setBudgetFileId:(NSString *)fileId groupId:(NSString *)groupId __attribute__((swift_name("setBudget(fileId:groupId:)")));
- (void)setTokenToken:(NSString *)token __attribute__((swift_name("setToken(token:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)syncWithCompletionHandler:(void (^)(ActualSyncSyncResult * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("sync(completionHandler:)")));
- (void)updateAccountId:(NSString *)id field:(NSString *)field value:(id _Nullable)value __attribute__((swift_name("updateAccount(id:field:value:)")));
- (void)updateCategoryId:(NSString *)id field:(NSString *)field value:(id _Nullable)value __attribute__((swift_name("updateCategory(id:field:value:)")));
- (void)updatePayeeId:(NSString *)id field:(NSString *)field value:(id _Nullable)value __attribute__((swift_name("updatePayee(id:field:value:)")));
- (void)updateTransactionId:(NSString *)id field:(NSString *)field value:(id _Nullable)value __attribute__((swift_name("updateTransaction(id:field:value:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SyncResponse_")))
@interface ActualSyncSyncResponse_ : ActualSyncBase
- (instancetype)initWithMessages:(NSArray<ActualSyncMessageEnvelope_ *> *)messages merkle:(ActualSyncTrieNode *)merkle __attribute__((swift_name("init(messages:merkle:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncSyncResponse_ *)doCopyMessages:(NSArray<ActualSyncMessageEnvelope_ *> *)messages merkle:(ActualSyncTrieNode *)merkle __attribute__((swift_name("doCopy(messages:merkle:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncTrieNode *merkle __attribute__((swift_name("merkle")));
@property (readonly) NSArray<ActualSyncMessageEnvelope_ *> *messages __attribute__((swift_name("messages")));
@end

__attribute__((swift_name("SyncResult")))
@interface ActualSyncSyncResult : ActualSyncBase
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SyncResult.Error")))
@interface ActualSyncSyncResultError : ActualSyncSyncResult
- (instancetype)initWithMessage:(NSString *)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncSyncResultError *)doCopyMessage:(NSString *)message __attribute__((swift_name("doCopy(message:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *message __attribute__((swift_name("message")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SyncResult.Success")))
@interface ActualSyncSyncResultSuccess : ActualSyncSyncResult
- (instancetype)initWithMessagesSent:(int32_t)messagesSent messagesReceived:(int32_t)messagesReceived messagesApplied:(int32_t)messagesApplied __attribute__((swift_name("init(messagesSent:messagesReceived:messagesApplied:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncSyncResultSuccess *)doCopyMessagesSent:(int32_t)messagesSent messagesReceived:(int32_t)messagesReceived messagesApplied:(int32_t)messagesApplied __attribute__((swift_name("doCopy(messagesSent:messagesReceived:messagesApplied:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int32_t messagesApplied __attribute__((swift_name("messagesApplied")));
@property (readonly) int32_t messagesReceived __attribute__((swift_name("messagesReceived")));
@property (readonly) int32_t messagesSent __attribute__((swift_name("messagesSent")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("DatabaseDriverFactoryKt")))
@interface ActualSyncDatabaseDriverFactoryKt : ActualSyncBase
+ (id<ActualSyncActualDatabase>)createDatabaseDriverFactory:(ActualSyncDatabaseDriverFactory *)driverFactory dbName:(NSString *)dbName __attribute__((swift_name("createDatabase(driverFactory:dbName:)")));
+ (id<ActualSyncActualDatabase>)createDatabaseForExistingDbName:(NSString *)dbName __attribute__((swift_name("createDatabaseForExisting(dbName:)")));
+ (id<ActualSyncRuntimeSqlDriver>)createDriverForExistingDbDbName:(NSString *)dbName __attribute__((swift_name("createDriverForExistingDb(dbName:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("HelpersKt")))
@interface ActualSyncHelpersKt : ActualSyncBase
+ (ActualSyncBudgetFileManager *)createBudgetFileManager __attribute__((swift_name("createBudgetFileManager()")));
+ (id<ActualSyncActualDatabase>)createDatabaseDbName:(NSString *)dbName __attribute__((swift_name("createDatabase(dbName:)")));
+ (id<ActualSyncActualDatabase>)createDatabaseForExistingFileDbName:(NSString *)dbName __attribute__((swift_name("createDatabaseForExistingFile(dbName:)")));
+ (ActualSyncKtor_client_coreHttpClient *)createHttpClient __attribute__((swift_name("createHttpClient()")));
+ (id<ActualSyncActualDatabase>)createOrOpenDatabaseDbName:(NSString *)dbName __attribute__((swift_name("createOrOpenDatabase(dbName:)")));
+ (ActualSyncSyncManager *)createSyncManagerServerUrl:(NSString *)serverUrl dbName:(NSString *)dbName __attribute__((swift_name("createSyncManager(serverUrl:dbName:)")));
+ (BOOL)databaseExistsDbName:(NSString *)dbName __attribute__((swift_name("databaseExists(dbName:)")));
+ (NSString *)getDatabasePathDbName:(NSString *)dbName __attribute__((swift_name("getDatabasePath(dbName:)")));
@end

__attribute__((swift_name("KotlinRuntimeException")))
@interface ActualSyncKotlinRuntimeException : ActualSyncKotlinException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("KotlinIllegalStateException")))
@interface ActualSyncKotlinIllegalStateException : ActualSyncKotlinRuntimeException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.4")
*/
__attribute__((swift_name("KotlinCancellationException")))
@interface ActualSyncKotlinCancellationException : ActualSyncKotlinIllegalStateException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("Kotlinx_serialization_coreSerializationStrategy")))
@protocol ActualSyncKotlinx_serialization_coreSerializationStrategy
@required
- (void)serializeEncoder:(id<ActualSyncKotlinx_serialization_coreEncoder>)encoder value:(id _Nullable)value __attribute__((swift_name("serialize(encoder:value:)")));
@property (readonly) id<ActualSyncKotlinx_serialization_coreSerialDescriptor> descriptor __attribute__((swift_name("descriptor")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreDeserializationStrategy")))
@protocol ActualSyncKotlinx_serialization_coreDeserializationStrategy
@required
- (id _Nullable)deserializeDecoder:(id<ActualSyncKotlinx_serialization_coreDecoder>)decoder __attribute__((swift_name("deserialize(decoder:)")));
@property (readonly) id<ActualSyncKotlinx_serialization_coreSerialDescriptor> descriptor __attribute__((swift_name("descriptor")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreKSerializer")))
@protocol ActualSyncKotlinx_serialization_coreKSerializer <ActualSyncKotlinx_serialization_coreSerializationStrategy, ActualSyncKotlinx_serialization_coreDeserializationStrategy>
@required
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinByteArray")))
@interface ActualSyncKotlinByteArray : ActualSyncBase
+ (instancetype)arrayWithSize:(int32_t)size __attribute__((swift_name("init(size:)")));
+ (instancetype)arrayWithSize:(int32_t)size init:(ActualSyncByte *(^)(ActualSyncInt *))init __attribute__((swift_name("init(size:init:)")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (int8_t)getIndex:(int32_t)index __attribute__((swift_name("get(index:)")));
- (ActualSyncKotlinByteIterator *)iterator __attribute__((swift_name("iterator()")));
- (void)setIndex:(int32_t)index value:(int8_t)value __attribute__((swift_name("set(index:value:)")));
@property (readonly) int32_t size __attribute__((swift_name("size")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinArray")))
@interface ActualSyncKotlinArray<T> : ActualSyncBase
+ (instancetype)arrayWithSize:(int32_t)size init:(T _Nullable (^)(ActualSyncInt *))init __attribute__((swift_name("init(size:init:)")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (T _Nullable)getIndex:(int32_t)index __attribute__((swift_name("get(index:)")));
- (id<ActualSyncKotlinIterator>)iterator __attribute__((swift_name("iterator()")));
- (void)setIndex:(int32_t)index value:(T _Nullable)value __attribute__((swift_name("set(index:value:)")));
@property (readonly) int32_t size __attribute__((swift_name("size")));
@end

__attribute__((swift_name("RuntimeTransactionCallbacks")))
@protocol ActualSyncRuntimeTransactionCallbacks
@required
- (void)afterCommitFunction:(void (^)(void))function __attribute__((swift_name("afterCommit(function:)")));
- (void)afterRollbackFunction:(void (^)(void))function __attribute__((swift_name("afterRollback(function:)")));
@end

__attribute__((swift_name("RuntimeTransactionWithoutReturn")))
@protocol ActualSyncRuntimeTransactionWithoutReturn <ActualSyncRuntimeTransactionCallbacks>
@required
- (void)rollback __attribute__((swift_name("rollback()")));
- (void)transactionBody:(void (^)(id<ActualSyncRuntimeTransactionWithoutReturn>))body __attribute__((swift_name("transaction(body:)")));
@end

__attribute__((swift_name("RuntimeTransactionWithReturn")))
@protocol ActualSyncRuntimeTransactionWithReturn <ActualSyncRuntimeTransactionCallbacks>
@required
- (void)rollbackReturnValue:(id _Nullable)returnValue __attribute__((swift_name("rollback(returnValue:)")));
- (id _Nullable)transactionBody_:(id _Nullable (^)(id<ActualSyncRuntimeTransactionWithReturn>))body __attribute__((swift_name("transaction(body_:)")));
@end

__attribute__((swift_name("RuntimeCloseable")))
@protocol ActualSyncRuntimeCloseable
@required
- (void)close __attribute__((swift_name("close()")));
@end

__attribute__((swift_name("RuntimeSqlDriver")))
@protocol ActualSyncRuntimeSqlDriver <ActualSyncRuntimeCloseable>
@required
- (void)addListenerQueryKeys:(ActualSyncKotlinArray<NSString *> *)queryKeys listener:(id<ActualSyncRuntimeQueryListener>)listener __attribute__((swift_name("addListener(queryKeys:listener:)")));
- (ActualSyncRuntimeTransacterTransaction * _Nullable)currentTransaction __attribute__((swift_name("currentTransaction()")));
- (id<ActualSyncRuntimeQueryResult>)executeIdentifier:(ActualSyncInt * _Nullable)identifier sql:(NSString *)sql parameters:(int32_t)parameters binders:(void (^ _Nullable)(id<ActualSyncRuntimeSqlPreparedStatement>))binders __attribute__((swift_name("execute(identifier:sql:parameters:binders:)")));
- (id<ActualSyncRuntimeQueryResult>)executeQueryIdentifier:(ActualSyncInt * _Nullable)identifier sql:(NSString *)sql mapper:(id<ActualSyncRuntimeQueryResult> (^)(id<ActualSyncRuntimeSqlCursor>))mapper parameters:(int32_t)parameters binders:(void (^ _Nullable)(id<ActualSyncRuntimeSqlPreparedStatement>))binders __attribute__((swift_name("executeQuery(identifier:sql:mapper:parameters:binders:)")));
- (id<ActualSyncRuntimeQueryResult>)doNewTransaction __attribute__((swift_name("doNewTransaction()")));
- (void)notifyListenersQueryKeys:(ActualSyncKotlinArray<NSString *> *)queryKeys __attribute__((swift_name("notifyListeners(queryKeys:)")));
- (void)removeListenerQueryKeys:(ActualSyncKotlinArray<NSString *> *)queryKeys listener:(id<ActualSyncRuntimeQueryListener>)listener __attribute__((swift_name("removeListener(queryKeys:listener:)")));
@end

__attribute__((swift_name("RuntimeSqlSchema")))
@protocol ActualSyncRuntimeSqlSchema
@required
- (id<ActualSyncRuntimeQueryResult>)createDriver:(id<ActualSyncRuntimeSqlDriver>)driver __attribute__((swift_name("create(driver:)")));
- (id<ActualSyncRuntimeQueryResult>)migrateDriver:(id<ActualSyncRuntimeSqlDriver>)driver oldVersion:(int64_t)oldVersion newVersion:(int64_t)newVersion callbacks:(ActualSyncKotlinArray<ActualSyncRuntimeAfterVersion *> *)callbacks __attribute__((swift_name("migrate(driver:oldVersion:newVersion:callbacks:)")));
@property (readonly) int64_t version __attribute__((swift_name("version")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinUnit")))
@interface ActualSyncKotlinUnit : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)unit __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKotlinUnit *shared __attribute__((swift_name("shared")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((swift_name("RuntimeTransacterTransaction")))
@interface ActualSyncRuntimeTransacterTransaction : ActualSyncBase <ActualSyncRuntimeTransactionCallbacks>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)afterCommitFunction:(void (^)(void))function __attribute__((swift_name("afterCommit(function:)")));
- (void)afterRollbackFunction:(void (^)(void))function __attribute__((swift_name("afterRollback(function:)")));

/**
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (id<ActualSyncRuntimeQueryResult>)endTransactionSuccessful:(BOOL)successful __attribute__((swift_name("endTransaction(successful:)")));

/**
 * @note This property has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
@property (readonly) ActualSyncRuntimeTransacterTransaction * _Nullable enclosingTransaction __attribute__((swift_name("enclosingTransaction")));
@end

__attribute__((swift_name("RuntimeExecutableQuery")))
@interface ActualSyncRuntimeExecutableQuery<__covariant RowType> : ActualSyncBase
- (instancetype)initWithMapper:(RowType (^)(id<ActualSyncRuntimeSqlCursor>))mapper __attribute__((swift_name("init(mapper:)"))) __attribute__((objc_designated_initializer));
- (id<ActualSyncRuntimeQueryResult>)executeMapper:(id<ActualSyncRuntimeQueryResult> (^)(id<ActualSyncRuntimeSqlCursor>))mapper __attribute__((swift_name("execute(mapper:)")));
- (NSArray<RowType> *)executeAsList __attribute__((swift_name("executeAsList()")));
- (RowType)executeAsOne __attribute__((swift_name("executeAsOne()")));
- (RowType _Nullable)executeAsOneOrNull __attribute__((swift_name("executeAsOneOrNull()")));
@property (readonly) RowType (^mapper)(id<ActualSyncRuntimeSqlCursor>) __attribute__((swift_name("mapper")));
@end

__attribute__((swift_name("RuntimeQuery")))
@interface ActualSyncRuntimeQuery<__covariant RowType> : ActualSyncRuntimeExecutableQuery<RowType>
- (instancetype)initWithMapper:(RowType (^)(id<ActualSyncRuntimeSqlCursor>))mapper __attribute__((swift_name("init(mapper:)"))) __attribute__((objc_designated_initializer));
- (void)addListenerListener:(id<ActualSyncRuntimeQueryListener>)listener __attribute__((swift_name("addListener(listener:)")));
- (void)removeListenerListener:(id<ActualSyncRuntimeQueryListener>)listener __attribute__((swift_name("removeListener(listener:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinPair")))
@interface ActualSyncKotlinPair<__covariant A, __covariant B> : ActualSyncBase
- (instancetype)initWithFirst:(A _Nullable)first second:(B _Nullable)second __attribute__((swift_name("init(first:second:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncKotlinPair<A, B> *)doCopyFirst:(A _Nullable)first second:(B _Nullable)second __attribute__((swift_name("doCopy(first:second:)")));
- (BOOL)equalsOther:(id _Nullable)other __attribute__((swift_name("equals(other:)")));
- (int32_t)hashCode __attribute__((swift_name("hashCode()")));
- (NSString *)toString __attribute__((swift_name("toString()")));
@property (readonly) A _Nullable first __attribute__((swift_name("first")));
@property (readonly) B _Nullable second __attribute__((swift_name("second")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreCoroutineScope")))
@protocol ActualSyncKotlinx_coroutines_coreCoroutineScope
@required
@property (readonly) id<ActualSyncKotlinCoroutineContext> coroutineContext __attribute__((swift_name("coroutineContext")));
@end

__attribute__((swift_name("Ktor_ioCloseable")))
@protocol ActualSyncKtor_ioCloseable
@required
- (void)close __attribute__((swift_name("close()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpClient")))
@interface ActualSyncKtor_client_coreHttpClient : ActualSyncBase <ActualSyncKotlinx_coroutines_coreCoroutineScope, ActualSyncKtor_ioCloseable>
- (instancetype)initWithEngine:(id<ActualSyncKtor_client_coreHttpClientEngine>)engine userConfig:(ActualSyncKtor_client_coreHttpClientConfig<ActualSyncKtor_client_coreHttpClientEngineConfig *> *)userConfig __attribute__((swift_name("init(engine:userConfig:)"))) __attribute__((objc_designated_initializer));
- (void)close __attribute__((swift_name("close()")));
- (ActualSyncKtor_client_coreHttpClient *)configBlock:(void (^)(ActualSyncKtor_client_coreHttpClientConfig<id> *))block __attribute__((swift_name("config(block:)")));
- (BOOL)isSupportedCapability:(id<ActualSyncKtor_client_coreHttpClientEngineCapability>)capability __attribute__((swift_name("isSupported(capability:)")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) id<ActualSyncKtor_utilsAttributes> attributes __attribute__((swift_name("attributes")));
@property (readonly) id<ActualSyncKotlinCoroutineContext> coroutineContext __attribute__((swift_name("coroutineContext")));
@property (readonly) id<ActualSyncKtor_client_coreHttpClientEngine> engine __attribute__((swift_name("engine")));
@property (readonly) ActualSyncKtor_client_coreHttpClientEngineConfig *engineConfig __attribute__((swift_name("engineConfig")));
@property (readonly) ActualSyncKtor_eventsEvents *monitor __attribute__((swift_name("monitor")));
@property (readonly) ActualSyncKtor_client_coreHttpReceivePipeline *receivePipeline __attribute__((swift_name("receivePipeline")));
@property (readonly) ActualSyncKtor_client_coreHttpRequestPipeline *requestPipeline __attribute__((swift_name("requestPipeline")));
@property (readonly) ActualSyncKtor_client_coreHttpResponsePipeline *responsePipeline __attribute__((swift_name("responsePipeline")));
@property (readonly) ActualSyncKtor_client_coreHttpSendPipeline *sendPipeline __attribute__((swift_name("sendPipeline")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreEncoder")))
@protocol ActualSyncKotlinx_serialization_coreEncoder
@required
- (id<ActualSyncKotlinx_serialization_coreCompositeEncoder>)beginCollectionDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor collectionSize:(int32_t)collectionSize __attribute__((swift_name("beginCollection(descriptor:collectionSize:)")));
- (id<ActualSyncKotlinx_serialization_coreCompositeEncoder>)beginStructureDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("beginStructure(descriptor:)")));
- (void)encodeBooleanValue:(BOOL)value __attribute__((swift_name("encodeBoolean(value:)")));
- (void)encodeByteValue:(int8_t)value __attribute__((swift_name("encodeByte(value:)")));
- (void)encodeCharValue:(unichar)value __attribute__((swift_name("encodeChar(value:)")));
- (void)encodeDoubleValue:(double)value __attribute__((swift_name("encodeDouble(value:)")));
- (void)encodeEnumEnumDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)enumDescriptor index:(int32_t)index __attribute__((swift_name("encodeEnum(enumDescriptor:index:)")));
- (void)encodeFloatValue:(float)value __attribute__((swift_name("encodeFloat(value:)")));
- (id<ActualSyncKotlinx_serialization_coreEncoder>)encodeInlineDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("encodeInline(descriptor:)")));
- (void)encodeIntValue:(int32_t)value __attribute__((swift_name("encodeInt(value:)")));
- (void)encodeLongValue:(int64_t)value __attribute__((swift_name("encodeLong(value:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (void)encodeNotNullMark __attribute__((swift_name("encodeNotNullMark()")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (void)encodeNull __attribute__((swift_name("encodeNull()")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (void)encodeNullableSerializableValueSerializer:(id<ActualSyncKotlinx_serialization_coreSerializationStrategy>)serializer value:(id _Nullable)value __attribute__((swift_name("encodeNullableSerializableValue(serializer:value:)")));
- (void)encodeSerializableValueSerializer:(id<ActualSyncKotlinx_serialization_coreSerializationStrategy>)serializer value:(id _Nullable)value __attribute__((swift_name("encodeSerializableValue(serializer:value:)")));
- (void)encodeShortValue:(int16_t)value __attribute__((swift_name("encodeShort(value:)")));
- (void)encodeStringValue:(NSString *)value __attribute__((swift_name("encodeString(value:)")));
@property (readonly) ActualSyncKotlinx_serialization_coreSerializersModule *serializersModule __attribute__((swift_name("serializersModule")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreSerialDescriptor")))
@protocol ActualSyncKotlinx_serialization_coreSerialDescriptor
@required

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (NSArray<id<ActualSyncKotlinAnnotation>> *)getElementAnnotationsIndex:(int32_t)index __attribute__((swift_name("getElementAnnotations(index:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)getElementDescriptorIndex:(int32_t)index __attribute__((swift_name("getElementDescriptor(index:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (int32_t)getElementIndexName:(NSString *)name __attribute__((swift_name("getElementIndex(name:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (NSString *)getElementNameIndex:(int32_t)index __attribute__((swift_name("getElementName(index:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (BOOL)isElementOptionalIndex:(int32_t)index __attribute__((swift_name("isElementOptional(index:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
@property (readonly) NSArray<id<ActualSyncKotlinAnnotation>> *annotations __attribute__((swift_name("annotations")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
@property (readonly) int32_t elementsCount __attribute__((swift_name("elementsCount")));
@property (readonly) BOOL isInline __attribute__((swift_name("isInline")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
@property (readonly) BOOL isNullable __attribute__((swift_name("isNullable")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
@property (readonly) ActualSyncKotlinx_serialization_coreSerialKind *kind __attribute__((swift_name("kind")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
@property (readonly) NSString *serialName __attribute__((swift_name("serialName")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreDecoder")))
@protocol ActualSyncKotlinx_serialization_coreDecoder
@required
- (id<ActualSyncKotlinx_serialization_coreCompositeDecoder>)beginStructureDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("beginStructure(descriptor:)")));
- (BOOL)decodeBoolean __attribute__((swift_name("decodeBoolean()")));
- (int8_t)decodeByte __attribute__((swift_name("decodeByte()")));
- (unichar)decodeChar __attribute__((swift_name("decodeChar()")));
- (double)decodeDouble __attribute__((swift_name("decodeDouble()")));
- (int32_t)decodeEnumEnumDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)enumDescriptor __attribute__((swift_name("decodeEnum(enumDescriptor:)")));
- (float)decodeFloat __attribute__((swift_name("decodeFloat()")));
- (id<ActualSyncKotlinx_serialization_coreDecoder>)decodeInlineDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("decodeInline(descriptor:)")));
- (int32_t)decodeInt __attribute__((swift_name("decodeInt()")));
- (int64_t)decodeLong __attribute__((swift_name("decodeLong()")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (BOOL)decodeNotNullMark __attribute__((swift_name("decodeNotNullMark()")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (ActualSyncKotlinNothing * _Nullable)decodeNull __attribute__((swift_name("decodeNull()")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (id _Nullable)decodeNullableSerializableValueDeserializer:(id<ActualSyncKotlinx_serialization_coreDeserializationStrategy>)deserializer __attribute__((swift_name("decodeNullableSerializableValue(deserializer:)")));
- (id _Nullable)decodeSerializableValueDeserializer:(id<ActualSyncKotlinx_serialization_coreDeserializationStrategy>)deserializer __attribute__((swift_name("decodeSerializableValue(deserializer:)")));
- (int16_t)decodeShort __attribute__((swift_name("decodeShort()")));
- (NSString *)decodeString __attribute__((swift_name("decodeString()")));
@property (readonly) ActualSyncKotlinx_serialization_coreSerializersModule *serializersModule __attribute__((swift_name("serializersModule")));
@end

__attribute__((swift_name("KotlinIterator")))
@protocol ActualSyncKotlinIterator
@required
- (BOOL)hasNext __attribute__((swift_name("hasNext()")));
- (id _Nullable)next __attribute__((swift_name("next()")));
@end

__attribute__((swift_name("KotlinByteIterator")))
@interface ActualSyncKotlinByteIterator : ActualSyncBase <ActualSyncKotlinIterator>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (ActualSyncByte *)next __attribute__((swift_name("next()")));
- (int8_t)nextByte __attribute__((swift_name("nextByte()")));
@end

__attribute__((swift_name("RuntimeQueryListener")))
@protocol ActualSyncRuntimeQueryListener
@required
- (void)queryResultsChanged __attribute__((swift_name("queryResultsChanged()")));
@end

__attribute__((swift_name("RuntimeQueryResult")))
@protocol ActualSyncRuntimeQueryResult
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)awaitWithCompletionHandler:(void (^)(id _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("await(completionHandler:)")));
@property (readonly) id _Nullable value __attribute__((swift_name("value")));
@end

__attribute__((swift_name("RuntimeSqlPreparedStatement")))
@protocol ActualSyncRuntimeSqlPreparedStatement
@required
- (void)bindBooleanIndex:(int32_t)index boolean:(ActualSyncBoolean * _Nullable)boolean __attribute__((swift_name("bindBoolean(index:boolean:)")));
- (void)bindBytesIndex:(int32_t)index bytes:(ActualSyncKotlinByteArray * _Nullable)bytes __attribute__((swift_name("bindBytes(index:bytes:)")));
- (void)bindDoubleIndex:(int32_t)index double:(ActualSyncDouble * _Nullable)double_ __attribute__((swift_name("bindDouble(index:double:)")));
- (void)bindLongIndex:(int32_t)index long:(ActualSyncLong * _Nullable)long_ __attribute__((swift_name("bindLong(index:long:)")));
- (void)bindStringIndex:(int32_t)index string:(NSString * _Nullable)string __attribute__((swift_name("bindString(index:string:)")));
@end

__attribute__((swift_name("RuntimeSqlCursor")))
@protocol ActualSyncRuntimeSqlCursor
@required
- (ActualSyncBoolean * _Nullable)getBooleanIndex:(int32_t)index __attribute__((swift_name("getBoolean(index:)")));
- (ActualSyncKotlinByteArray * _Nullable)getBytesIndex:(int32_t)index __attribute__((swift_name("getBytes(index:)")));
- (ActualSyncDouble * _Nullable)getDoubleIndex:(int32_t)index __attribute__((swift_name("getDouble(index:)")));
- (ActualSyncLong * _Nullable)getLongIndex:(int32_t)index __attribute__((swift_name("getLong(index:)")));
- (NSString * _Nullable)getStringIndex:(int32_t)index __attribute__((swift_name("getString(index:)")));
- (id<ActualSyncRuntimeQueryResult>)next __attribute__((swift_name("next()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("RuntimeAfterVersion")))
@interface ActualSyncRuntimeAfterVersion : ActualSyncBase
- (instancetype)initWithAfterVersion:(int64_t)afterVersion block:(void (^)(id<ActualSyncRuntimeSqlDriver>))block __attribute__((swift_name("init(afterVersion:block:)"))) __attribute__((objc_designated_initializer));
@property (readonly) int64_t afterVersion __attribute__((swift_name("afterVersion")));
@property (readonly) void (^block)(id<ActualSyncRuntimeSqlDriver>) __attribute__((swift_name("block")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.3")
*/
__attribute__((swift_name("KotlinCoroutineContext")))
@protocol ActualSyncKotlinCoroutineContext
@required
- (id _Nullable)foldInitial:(id _Nullable)initial operation:(id _Nullable (^)(id _Nullable, id<ActualSyncKotlinCoroutineContextElement>))operation __attribute__((swift_name("fold(initial:operation:)")));
- (id<ActualSyncKotlinCoroutineContextElement> _Nullable)getKey:(id<ActualSyncKotlinCoroutineContextKey>)key __attribute__((swift_name("get(key:)")));
- (id<ActualSyncKotlinCoroutineContext>)minusKeyKey:(id<ActualSyncKotlinCoroutineContextKey>)key __attribute__((swift_name("minusKey(key:)")));
- (id<ActualSyncKotlinCoroutineContext>)plusContext:(id<ActualSyncKotlinCoroutineContext>)context __attribute__((swift_name("plus(context:)")));
@end

__attribute__((swift_name("Ktor_client_coreHttpClientEngine")))
@protocol ActualSyncKtor_client_coreHttpClientEngine <ActualSyncKotlinx_coroutines_coreCoroutineScope, ActualSyncKtor_ioCloseable>
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)executeData:(ActualSyncKtor_client_coreHttpRequestData *)data completionHandler:(void (^)(ActualSyncKtor_client_coreHttpResponseData * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("execute(data:completionHandler:)")));
- (void)installClient:(ActualSyncKtor_client_coreHttpClient *)client __attribute__((swift_name("install(client:)")));
@property (readonly) ActualSyncKtor_client_coreHttpClientEngineConfig *config __attribute__((swift_name("config")));
@property (readonly) ActualSyncKotlinx_coroutines_coreCoroutineDispatcher *dispatcher __attribute__((swift_name("dispatcher")));
@property (readonly) NSSet<id<ActualSyncKtor_client_coreHttpClientEngineCapability>> *supportedCapabilities __attribute__((swift_name("supportedCapabilities")));
@end

__attribute__((swift_name("Ktor_client_coreHttpClientEngineConfig")))
@interface ActualSyncKtor_client_coreHttpClientEngineConfig : ActualSyncBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
@property ActualSyncKotlinx_coroutines_coreCoroutineDispatcher * _Nullable dispatcher __attribute__((swift_name("dispatcher")));
@property BOOL pipelining __attribute__((swift_name("pipelining")));
@property ActualSyncKtor_client_coreProxyConfig * _Nullable proxy __attribute__((swift_name("proxy")));
@property int32_t threadsCount __attribute__((swift_name("threadsCount"))) __attribute__((unavailable("The [threadsCount] property is deprecated. Consider setting [dispatcher] instead.")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpClientConfig")))
@interface ActualSyncKtor_client_coreHttpClientConfig<T> : ActualSyncBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (ActualSyncKtor_client_coreHttpClientConfig<T> *)clone __attribute__((swift_name("clone()")));
- (void)engineBlock:(void (^)(T))block __attribute__((swift_name("engine(block:)")));
- (void)installClient:(ActualSyncKtor_client_coreHttpClient *)client __attribute__((swift_name("install(client:)")));
- (void)installPlugin:(id<ActualSyncKtor_client_coreHttpClientPlugin>)plugin configure:(void (^)(id))configure __attribute__((swift_name("install(plugin:configure:)")));
- (void)installKey:(NSString *)key block:(void (^)(ActualSyncKtor_client_coreHttpClient *))block __attribute__((swift_name("install(key:block:)")));
- (void)plusAssignOther:(ActualSyncKtor_client_coreHttpClientConfig<T> *)other __attribute__((swift_name("plusAssign(other:)")));
@property BOOL developmentMode __attribute__((swift_name("developmentMode"))) __attribute__((deprecated("Development mode is no longer required. The property will be removed in the future.")));
@property BOOL expectSuccess __attribute__((swift_name("expectSuccess")));
@property BOOL followRedirects __attribute__((swift_name("followRedirects")));
@property BOOL useDefaultTransformers __attribute__((swift_name("useDefaultTransformers")));
@end

__attribute__((swift_name("Ktor_client_coreHttpClientEngineCapability")))
@protocol ActualSyncKtor_client_coreHttpClientEngineCapability
@required
@end

__attribute__((swift_name("Ktor_utilsAttributes")))
@protocol ActualSyncKtor_utilsAttributes
@required
- (id)computeIfAbsentKey:(ActualSyncKtor_utilsAttributeKey<id> *)key block:(id (^)(void))block __attribute__((swift_name("computeIfAbsent(key:block:)")));
- (BOOL)containsKey:(ActualSyncKtor_utilsAttributeKey<id> *)key __attribute__((swift_name("contains(key:)")));
- (id)getKey_:(ActualSyncKtor_utilsAttributeKey<id> *)key __attribute__((swift_name("get(key_:)")));
- (id _Nullable)getOrNullKey:(ActualSyncKtor_utilsAttributeKey<id> *)key __attribute__((swift_name("getOrNull(key:)")));
- (void)putKey:(ActualSyncKtor_utilsAttributeKey<id> *)key value:(id)value __attribute__((swift_name("put(key:value:)")));
- (void)removeKey:(ActualSyncKtor_utilsAttributeKey<id> *)key __attribute__((swift_name("remove(key:)")));
- (id)takeKey:(ActualSyncKtor_utilsAttributeKey<id> *)key __attribute__((swift_name("take(key:)")));
- (id _Nullable)takeOrNullKey:(ActualSyncKtor_utilsAttributeKey<id> *)key __attribute__((swift_name("takeOrNull(key:)")));
@property (readonly) NSArray<ActualSyncKtor_utilsAttributeKey<id> *> *allKeys __attribute__((swift_name("allKeys")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_eventsEvents")))
@interface ActualSyncKtor_eventsEvents : ActualSyncBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)raiseDefinition:(ActualSyncKtor_eventsEventDefinition<id> *)definition value:(id _Nullable)value __attribute__((swift_name("raise(definition:value:)")));
- (id<ActualSyncKotlinx_coroutines_coreDisposableHandle>)subscribeDefinition:(ActualSyncKtor_eventsEventDefinition<id> *)definition handler:(void (^)(id _Nullable))handler __attribute__((swift_name("subscribe(definition:handler:)")));
- (void)unsubscribeDefinition:(ActualSyncKtor_eventsEventDefinition<id> *)definition handler:(void (^)(id _Nullable))handler __attribute__((swift_name("unsubscribe(definition:handler:)")));
@end

__attribute__((swift_name("Ktor_utilsPipeline")))
@interface ActualSyncKtor_utilsPipeline<TSubject, TContext> : ActualSyncBase
- (instancetype)initWithPhases:(ActualSyncKotlinArray<ActualSyncKtor_utilsPipelinePhase *> *)phases __attribute__((swift_name("init(phases:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithPhase:(ActualSyncKtor_utilsPipelinePhase *)phase interceptors:(NSArray<id<ActualSyncKotlinSuspendFunction2>> *)interceptors __attribute__((swift_name("init(phase:interceptors:)"))) __attribute__((objc_designated_initializer));
- (void)addPhasePhase:(ActualSyncKtor_utilsPipelinePhase *)phase __attribute__((swift_name("addPhase(phase:)")));
- (void)afterIntercepted __attribute__((swift_name("afterIntercepted()")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)executeContext:(TContext)context subject:(TSubject)subject completionHandler:(void (^)(TSubject _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("execute(context:subject:completionHandler:)")));
- (void)insertPhaseAfterReference:(ActualSyncKtor_utilsPipelinePhase *)reference phase:(ActualSyncKtor_utilsPipelinePhase *)phase __attribute__((swift_name("insertPhaseAfter(reference:phase:)")));
- (void)insertPhaseBeforeReference:(ActualSyncKtor_utilsPipelinePhase *)reference phase:(ActualSyncKtor_utilsPipelinePhase *)phase __attribute__((swift_name("insertPhaseBefore(reference:phase:)")));
- (void)interceptPhase:(ActualSyncKtor_utilsPipelinePhase *)phase block:(id<ActualSyncKotlinSuspendFunction2>)block __attribute__((swift_name("intercept(phase:block:)")));
- (NSArray<id<ActualSyncKotlinSuspendFunction2>> *)interceptorsForPhasePhase:(ActualSyncKtor_utilsPipelinePhase *)phase __attribute__((swift_name("interceptorsForPhase(phase:)")));
- (void)mergeFrom:(ActualSyncKtor_utilsPipeline<TSubject, TContext> *)from __attribute__((swift_name("merge(from:)")));
- (void)mergePhasesFrom:(ActualSyncKtor_utilsPipeline<TSubject, TContext> *)from __attribute__((swift_name("mergePhases(from:)")));
- (void)resetFromFrom:(ActualSyncKtor_utilsPipeline<TSubject, TContext> *)from __attribute__((swift_name("resetFrom(from:)")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) id<ActualSyncKtor_utilsAttributes> attributes __attribute__((swift_name("attributes")));
@property (readonly) BOOL developmentMode __attribute__((swift_name("developmentMode")));
@property (readonly) BOOL isEmpty __attribute__((swift_name("isEmpty")));
@property (readonly) NSArray<ActualSyncKtor_utilsPipelinePhase *> *items __attribute__((swift_name("items")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpReceivePipeline")))
@interface ActualSyncKtor_client_coreHttpReceivePipeline : ActualSyncKtor_utilsPipeline<ActualSyncKtor_client_coreHttpResponse *, ActualSyncKotlinUnit *>
- (instancetype)initWithDevelopmentMode:(BOOL)developmentMode __attribute__((swift_name("init(developmentMode:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithPhases:(ActualSyncKotlinArray<ActualSyncKtor_utilsPipelinePhase *> *)phases __attribute__((swift_name("init(phases:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithPhase:(ActualSyncKtor_utilsPipelinePhase *)phase interceptors:(NSArray<id<ActualSyncKotlinSuspendFunction2>> *)interceptors __attribute__((swift_name("init(phase:interceptors:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly, getter=companion) ActualSyncKtor_client_coreHttpReceivePipelinePhases *companion __attribute__((swift_name("companion")));
@property (readonly) BOOL developmentMode __attribute__((swift_name("developmentMode")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpRequestPipeline")))
@interface ActualSyncKtor_client_coreHttpRequestPipeline : ActualSyncKtor_utilsPipeline<id, ActualSyncKtor_client_coreHttpRequestBuilder *>
- (instancetype)initWithDevelopmentMode:(BOOL)developmentMode __attribute__((swift_name("init(developmentMode:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithPhases:(ActualSyncKotlinArray<ActualSyncKtor_utilsPipelinePhase *> *)phases __attribute__((swift_name("init(phases:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithPhase:(ActualSyncKtor_utilsPipelinePhase *)phase interceptors:(NSArray<id<ActualSyncKotlinSuspendFunction2>> *)interceptors __attribute__((swift_name("init(phase:interceptors:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly, getter=companion) ActualSyncKtor_client_coreHttpRequestPipelinePhases *companion __attribute__((swift_name("companion")));
@property (readonly) BOOL developmentMode __attribute__((swift_name("developmentMode")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpResponsePipeline")))
@interface ActualSyncKtor_client_coreHttpResponsePipeline : ActualSyncKtor_utilsPipeline<ActualSyncKtor_client_coreHttpResponseContainer *, ActualSyncKtor_client_coreHttpClientCall *>
- (instancetype)initWithDevelopmentMode:(BOOL)developmentMode __attribute__((swift_name("init(developmentMode:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithPhases:(ActualSyncKotlinArray<ActualSyncKtor_utilsPipelinePhase *> *)phases __attribute__((swift_name("init(phases:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithPhase:(ActualSyncKtor_utilsPipelinePhase *)phase interceptors:(NSArray<id<ActualSyncKotlinSuspendFunction2>> *)interceptors __attribute__((swift_name("init(phase:interceptors:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly, getter=companion) ActualSyncKtor_client_coreHttpResponsePipelinePhases *companion __attribute__((swift_name("companion")));
@property (readonly) BOOL developmentMode __attribute__((swift_name("developmentMode")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpSendPipeline")))
@interface ActualSyncKtor_client_coreHttpSendPipeline : ActualSyncKtor_utilsPipeline<id, ActualSyncKtor_client_coreHttpRequestBuilder *>
- (instancetype)initWithDevelopmentMode:(BOOL)developmentMode __attribute__((swift_name("init(developmentMode:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithPhases:(ActualSyncKotlinArray<ActualSyncKtor_utilsPipelinePhase *> *)phases __attribute__((swift_name("init(phases:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithPhase:(ActualSyncKtor_utilsPipelinePhase *)phase interceptors:(NSArray<id<ActualSyncKotlinSuspendFunction2>> *)interceptors __attribute__((swift_name("init(phase:interceptors:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly, getter=companion) ActualSyncKtor_client_coreHttpSendPipelinePhases *companion __attribute__((swift_name("companion")));
@property (readonly) BOOL developmentMode __attribute__((swift_name("developmentMode")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreCompositeEncoder")))
@protocol ActualSyncKotlinx_serialization_coreCompositeEncoder
@required
- (void)encodeBooleanElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(BOOL)value __attribute__((swift_name("encodeBooleanElement(descriptor:index:value:)")));
- (void)encodeByteElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(int8_t)value __attribute__((swift_name("encodeByteElement(descriptor:index:value:)")));
- (void)encodeCharElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(unichar)value __attribute__((swift_name("encodeCharElement(descriptor:index:value:)")));
- (void)encodeDoubleElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(double)value __attribute__((swift_name("encodeDoubleElement(descriptor:index:value:)")));
- (void)encodeFloatElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(float)value __attribute__((swift_name("encodeFloatElement(descriptor:index:value:)")));
- (id<ActualSyncKotlinx_serialization_coreEncoder>)encodeInlineElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("encodeInlineElement(descriptor:index:)")));
- (void)encodeIntElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(int32_t)value __attribute__((swift_name("encodeIntElement(descriptor:index:value:)")));
- (void)encodeLongElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(int64_t)value __attribute__((swift_name("encodeLongElement(descriptor:index:value:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (void)encodeNullableSerializableElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index serializer:(id<ActualSyncKotlinx_serialization_coreSerializationStrategy>)serializer value:(id _Nullable)value __attribute__((swift_name("encodeNullableSerializableElement(descriptor:index:serializer:value:)")));
- (void)encodeSerializableElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index serializer:(id<ActualSyncKotlinx_serialization_coreSerializationStrategy>)serializer value:(id _Nullable)value __attribute__((swift_name("encodeSerializableElement(descriptor:index:serializer:value:)")));
- (void)encodeShortElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(int16_t)value __attribute__((swift_name("encodeShortElement(descriptor:index:value:)")));
- (void)encodeStringElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index value:(NSString *)value __attribute__((swift_name("encodeStringElement(descriptor:index:value:)")));
- (void)endStructureDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("endStructure(descriptor:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (BOOL)shouldEncodeElementDefaultDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("shouldEncodeElementDefault(descriptor:index:)")));
@property (readonly) ActualSyncKotlinx_serialization_coreSerializersModule *serializersModule __attribute__((swift_name("serializersModule")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreSerializersModule")))
@interface ActualSyncKotlinx_serialization_coreSerializersModule : ActualSyncBase

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (void)dumpToCollector:(id<ActualSyncKotlinx_serialization_coreSerializersModuleCollector>)collector __attribute__((swift_name("dumpTo(collector:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (id<ActualSyncKotlinx_serialization_coreKSerializer> _Nullable)getContextualKClass:(id<ActualSyncKotlinKClass>)kClass typeArgumentsSerializers:(NSArray<id<ActualSyncKotlinx_serialization_coreKSerializer>> *)typeArgumentsSerializers __attribute__((swift_name("getContextual(kClass:typeArgumentsSerializers:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (id<ActualSyncKotlinx_serialization_coreSerializationStrategy> _Nullable)getPolymorphicBaseClass:(id<ActualSyncKotlinKClass>)baseClass value:(id)value __attribute__((swift_name("getPolymorphic(baseClass:value:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (id<ActualSyncKotlinx_serialization_coreDeserializationStrategy> _Nullable)getPolymorphicBaseClass:(id<ActualSyncKotlinKClass>)baseClass serializedClassName:(NSString * _Nullable)serializedClassName __attribute__((swift_name("getPolymorphic(baseClass:serializedClassName:)")));
@end

__attribute__((swift_name("KotlinAnnotation")))
@protocol ActualSyncKotlinAnnotation
@required
@end


/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
__attribute__((swift_name("Kotlinx_serialization_coreSerialKind")))
@interface ActualSyncKotlinx_serialization_coreSerialKind : ActualSyncBase
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((swift_name("Kotlinx_serialization_coreCompositeDecoder")))
@protocol ActualSyncKotlinx_serialization_coreCompositeDecoder
@required
- (BOOL)decodeBooleanElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeBooleanElement(descriptor:index:)")));
- (int8_t)decodeByteElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeByteElement(descriptor:index:)")));
- (unichar)decodeCharElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeCharElement(descriptor:index:)")));
- (int32_t)decodeCollectionSizeDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("decodeCollectionSize(descriptor:)")));
- (double)decodeDoubleElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeDoubleElement(descriptor:index:)")));
- (int32_t)decodeElementIndexDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("decodeElementIndex(descriptor:)")));
- (float)decodeFloatElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeFloatElement(descriptor:index:)")));
- (id<ActualSyncKotlinx_serialization_coreDecoder>)decodeInlineElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeInlineElement(descriptor:index:)")));
- (int32_t)decodeIntElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeIntElement(descriptor:index:)")));
- (int64_t)decodeLongElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeLongElement(descriptor:index:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (id _Nullable)decodeNullableSerializableElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index deserializer:(id<ActualSyncKotlinx_serialization_coreDeserializationStrategy>)deserializer previousValue:(id _Nullable)previousValue __attribute__((swift_name("decodeNullableSerializableElement(descriptor:index:deserializer:previousValue:)")));

/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
- (BOOL)decodeSequentially __attribute__((swift_name("decodeSequentially()")));
- (id _Nullable)decodeSerializableElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index deserializer:(id<ActualSyncKotlinx_serialization_coreDeserializationStrategy>)deserializer previousValue:(id _Nullable)previousValue __attribute__((swift_name("decodeSerializableElement(descriptor:index:deserializer:previousValue:)")));
- (int16_t)decodeShortElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeShortElement(descriptor:index:)")));
- (NSString *)decodeStringElementDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor index:(int32_t)index __attribute__((swift_name("decodeStringElement(descriptor:index:)")));
- (void)endStructureDescriptor:(id<ActualSyncKotlinx_serialization_coreSerialDescriptor>)descriptor __attribute__((swift_name("endStructure(descriptor:)")));
@property (readonly) ActualSyncKotlinx_serialization_coreSerializersModule *serializersModule __attribute__((swift_name("serializersModule")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinNothing")))
@interface ActualSyncKotlinNothing : ActualSyncBase
@end

__attribute__((swift_name("KotlinCoroutineContextElement")))
@protocol ActualSyncKotlinCoroutineContextElement <ActualSyncKotlinCoroutineContext>
@required
@property (readonly) id<ActualSyncKotlinCoroutineContextKey> key __attribute__((swift_name("key")));
@end

__attribute__((swift_name("KotlinCoroutineContextKey")))
@protocol ActualSyncKotlinCoroutineContextKey
@required
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpRequestData")))
@interface ActualSyncKtor_client_coreHttpRequestData : ActualSyncBase
- (instancetype)initWithUrl:(ActualSyncKtor_httpUrl *)url method:(ActualSyncKtor_httpHttpMethod *)method headers:(id<ActualSyncKtor_httpHeaders>)headers body:(ActualSyncKtor_httpOutgoingContent *)body executionContext:(id<ActualSyncKotlinx_coroutines_coreJob>)executionContext attributes:(id<ActualSyncKtor_utilsAttributes>)attributes __attribute__((swift_name("init(url:method:headers:body:executionContext:attributes:)"))) __attribute__((objc_designated_initializer));
- (id _Nullable)getCapabilityOrNullKey:(id<ActualSyncKtor_client_coreHttpClientEngineCapability>)key __attribute__((swift_name("getCapabilityOrNull(key:)")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) id<ActualSyncKtor_utilsAttributes> attributes __attribute__((swift_name("attributes")));
@property (readonly) ActualSyncKtor_httpOutgoingContent *body __attribute__((swift_name("body")));
@property (readonly) id<ActualSyncKotlinx_coroutines_coreJob> executionContext __attribute__((swift_name("executionContext")));
@property (readonly) id<ActualSyncKtor_httpHeaders> headers __attribute__((swift_name("headers")));
@property (readonly) ActualSyncKtor_httpHttpMethod *method __attribute__((swift_name("method")));
@property (readonly) ActualSyncKtor_httpUrl *url __attribute__((swift_name("url")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpResponseData")))
@interface ActualSyncKtor_client_coreHttpResponseData : ActualSyncBase
- (instancetype)initWithStatusCode:(ActualSyncKtor_httpHttpStatusCode *)statusCode requestTime:(ActualSyncKtor_utilsGMTDate *)requestTime headers:(id<ActualSyncKtor_httpHeaders>)headers version:(ActualSyncKtor_httpHttpProtocolVersion *)version body:(id)body callContext:(id<ActualSyncKotlinCoroutineContext>)callContext __attribute__((swift_name("init(statusCode:requestTime:headers:version:body:callContext:)"))) __attribute__((objc_designated_initializer));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) id body __attribute__((swift_name("body")));
@property (readonly) id<ActualSyncKotlinCoroutineContext> callContext __attribute__((swift_name("callContext")));
@property (readonly) id<ActualSyncKtor_httpHeaders> headers __attribute__((swift_name("headers")));
@property (readonly) ActualSyncKtor_utilsGMTDate *requestTime __attribute__((swift_name("requestTime")));
@property (readonly) ActualSyncKtor_utilsGMTDate *responseTime __attribute__((swift_name("responseTime")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *statusCode __attribute__((swift_name("statusCode")));
@property (readonly) ActualSyncKtor_httpHttpProtocolVersion *version __attribute__((swift_name("version")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.3")
*/
__attribute__((swift_name("KotlinAbstractCoroutineContextElement")))
@interface ActualSyncKotlinAbstractCoroutineContextElement : ActualSyncBase <ActualSyncKotlinCoroutineContextElement>
- (instancetype)initWithKey:(id<ActualSyncKotlinCoroutineContextKey>)key __attribute__((swift_name("init(key:)"))) __attribute__((objc_designated_initializer));
@property (readonly) id<ActualSyncKotlinCoroutineContextKey> key __attribute__((swift_name("key")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.3")
*/
__attribute__((swift_name("KotlinContinuationInterceptor")))
@protocol ActualSyncKotlinContinuationInterceptor <ActualSyncKotlinCoroutineContextElement>
@required
- (id<ActualSyncKotlinContinuation>)interceptContinuationContinuation:(id<ActualSyncKotlinContinuation>)continuation __attribute__((swift_name("interceptContinuation(continuation:)")));
- (void)releaseInterceptedContinuationContinuation:(id<ActualSyncKotlinContinuation>)continuation __attribute__((swift_name("releaseInterceptedContinuation(continuation:)")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreCoroutineDispatcher")))
@interface ActualSyncKotlinx_coroutines_coreCoroutineDispatcher : ActualSyncKotlinAbstractCoroutineContextElement <ActualSyncKotlinContinuationInterceptor>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithKey:(id<ActualSyncKotlinCoroutineContextKey>)key __attribute__((swift_name("init(key:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly, getter=companion) ActualSyncKotlinx_coroutines_coreCoroutineDispatcherKey *companion __attribute__((swift_name("companion")));
- (void)dispatchContext:(id<ActualSyncKotlinCoroutineContext>)context block:(id<ActualSyncKotlinx_coroutines_coreRunnable>)block __attribute__((swift_name("dispatch(context:block:)")));

/**
 * @note annotations
 *   kotlinx.coroutines.InternalCoroutinesApi
*/
- (void)dispatchYieldContext:(id<ActualSyncKotlinCoroutineContext>)context block:(id<ActualSyncKotlinx_coroutines_coreRunnable>)block __attribute__((swift_name("dispatchYield(context:block:)")));
- (id<ActualSyncKotlinContinuation>)interceptContinuationContinuation:(id<ActualSyncKotlinContinuation>)continuation __attribute__((swift_name("interceptContinuation(continuation:)")));
- (BOOL)isDispatchNeededContext:(id<ActualSyncKotlinCoroutineContext>)context __attribute__((swift_name("isDispatchNeeded(context:)")));
- (ActualSyncKotlinx_coroutines_coreCoroutineDispatcher *)limitedParallelismParallelism:(int32_t)parallelism name:(NSString * _Nullable)name __attribute__((swift_name("limitedParallelism(parallelism:name:)")));
- (ActualSyncKotlinx_coroutines_coreCoroutineDispatcher *)plusOther:(ActualSyncKotlinx_coroutines_coreCoroutineDispatcher *)other __attribute__((swift_name("plus(other:)"))) __attribute__((unavailable("Operator '+' on two CoroutineDispatcher objects is meaningless. CoroutineDispatcher is a coroutine context element and `+` is a set-sum operator for coroutine contexts. The dispatcher to the right of `+` just replaces the dispatcher to the left.")));
- (void)releaseInterceptedContinuationContinuation:(id<ActualSyncKotlinContinuation>)continuation __attribute__((swift_name("releaseInterceptedContinuation(continuation:)")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreProxyConfig")))
@interface ActualSyncKtor_client_coreProxyConfig : ActualSyncBase
- (instancetype)initWithUrl:(ActualSyncKtor_httpUrl *)url __attribute__((swift_name("init(url:)"))) __attribute__((objc_designated_initializer));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncKtor_httpUrl *url __attribute__((swift_name("url")));
@end

__attribute__((swift_name("Ktor_client_coreHttpClientPlugin")))
@protocol ActualSyncKtor_client_coreHttpClientPlugin
@required
- (void)installPlugin:(id)plugin scope:(ActualSyncKtor_client_coreHttpClient *)scope __attribute__((swift_name("install(plugin:scope:)")));
- (id)prepareBlock:(void (^)(id))block __attribute__((swift_name("prepare(block:)")));
@property (readonly) ActualSyncKtor_utilsAttributeKey<id> *key __attribute__((swift_name("key")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_utilsAttributeKey")))
@interface ActualSyncKtor_utilsAttributeKey<T> : ActualSyncBase

/**
 * @note annotations
 *   kotlin.jvm.JvmOverloads
*/
- (instancetype)initWithName:(NSString *)name type:(ActualSyncKtor_utilsTypeInfo *)type __attribute__((swift_name("init(name:type:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncKtor_utilsAttributeKey<T> *)doCopyName:(NSString *)name type:(ActualSyncKtor_utilsTypeInfo *)type __attribute__((swift_name("doCopy(name:type:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@end

__attribute__((swift_name("Ktor_eventsEventDefinition")))
@interface ActualSyncKtor_eventsEventDefinition<T> : ActualSyncBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreDisposableHandle")))
@protocol ActualSyncKotlinx_coroutines_coreDisposableHandle
@required
- (void)dispose __attribute__((swift_name("dispose()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_utilsPipelinePhase")))
@interface ActualSyncKtor_utilsPipelinePhase : ActualSyncBase
- (instancetype)initWithName:(NSString *)name __attribute__((swift_name("init(name:)"))) __attribute__((objc_designated_initializer));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@end

__attribute__((swift_name("KotlinFunction")))
@protocol ActualSyncKotlinFunction
@required
@end

__attribute__((swift_name("KotlinSuspendFunction2")))
@protocol ActualSyncKotlinSuspendFunction2 <ActualSyncKotlinFunction>
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)invokeP1:(id _Nullable)p1 p2:(id _Nullable)p2 completionHandler:(void (^)(id _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("invoke(p1:p2:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpReceivePipeline.Phases")))
@interface ActualSyncKtor_client_coreHttpReceivePipelinePhases : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)phases __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_client_coreHttpReceivePipelinePhases *shared __attribute__((swift_name("shared")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *After __attribute__((swift_name("After")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *Before __attribute__((swift_name("Before")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *State __attribute__((swift_name("State")));
@end

__attribute__((swift_name("Ktor_httpHttpMessage")))
@protocol ActualSyncKtor_httpHttpMessage
@required
@property (readonly) id<ActualSyncKtor_httpHeaders> headers __attribute__((swift_name("headers")));
@end

__attribute__((swift_name("Ktor_client_coreHttpResponse")))
@interface ActualSyncKtor_client_coreHttpResponse : ActualSyncBase <ActualSyncKtor_httpHttpMessage, ActualSyncKotlinx_coroutines_coreCoroutineScope>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncKtor_client_coreHttpClientCall *call __attribute__((swift_name("call")));
@property (readonly) id<ActualSyncKtor_ioByteReadChannel> rawContent __attribute__((swift_name("rawContent")));
@property (readonly) ActualSyncKtor_utilsGMTDate *requestTime __attribute__((swift_name("requestTime")));
@property (readonly) ActualSyncKtor_utilsGMTDate *responseTime __attribute__((swift_name("responseTime")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *status __attribute__((swift_name("status")));
@property (readonly) ActualSyncKtor_httpHttpProtocolVersion *version_ __attribute__((swift_name("version_")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpRequestPipeline.Phases")))
@interface ActualSyncKtor_client_coreHttpRequestPipelinePhases : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)phases __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_client_coreHttpRequestPipelinePhases *shared __attribute__((swift_name("shared")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *Before __attribute__((swift_name("Before")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *Render __attribute__((swift_name("Render")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *Send __attribute__((swift_name("Send")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *State __attribute__((swift_name("State")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *Transform __attribute__((swift_name("Transform")));
@end

__attribute__((swift_name("Ktor_httpHttpMessageBuilder")))
@protocol ActualSyncKtor_httpHttpMessageBuilder
@required
@property (readonly) ActualSyncKtor_httpHeadersBuilder *headers __attribute__((swift_name("headers")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpRequestBuilder")))
@interface ActualSyncKtor_client_coreHttpRequestBuilder : ActualSyncBase <ActualSyncKtor_httpHttpMessageBuilder>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
@property (class, readonly, getter=companion) ActualSyncKtor_client_coreHttpRequestBuilderCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncKtor_client_coreHttpRequestData *)build __attribute__((swift_name("build()")));
- (id _Nullable)getCapabilityOrNullKey:(id<ActualSyncKtor_client_coreHttpClientEngineCapability>)key __attribute__((swift_name("getCapabilityOrNull(key:)")));
- (void)setAttributesBlock:(void (^)(id<ActualSyncKtor_utilsAttributes>))block __attribute__((swift_name("setAttributes(block:)")));
- (void)setCapabilityKey:(id<ActualSyncKtor_client_coreHttpClientEngineCapability>)key capability:(id)capability __attribute__((swift_name("setCapability(key:capability:)")));
- (ActualSyncKtor_client_coreHttpRequestBuilder *)takeFromBuilder:(ActualSyncKtor_client_coreHttpRequestBuilder *)builder __attribute__((swift_name("takeFrom(builder:)")));
- (ActualSyncKtor_client_coreHttpRequestBuilder *)takeFromWithExecutionContextBuilder:(ActualSyncKtor_client_coreHttpRequestBuilder *)builder __attribute__((swift_name("takeFromWithExecutionContext(builder:)")));
- (void)urlBlock:(void (^)(ActualSyncKtor_httpURLBuilder *, ActualSyncKtor_httpURLBuilder *))block __attribute__((swift_name("url(block:)")));
@property (readonly) id<ActualSyncKtor_utilsAttributes> attributes __attribute__((swift_name("attributes")));
@property id body __attribute__((swift_name("body")));
@property ActualSyncKtor_utilsTypeInfo * _Nullable bodyType __attribute__((swift_name("bodyType")));
@property (readonly) id<ActualSyncKotlinx_coroutines_coreJob> executionContext __attribute__((swift_name("executionContext")));
@property (readonly) ActualSyncKtor_httpHeadersBuilder *headers __attribute__((swift_name("headers")));
@property ActualSyncKtor_httpHttpMethod *method __attribute__((swift_name("method")));
@property (readonly) ActualSyncKtor_httpURLBuilder *url __attribute__((swift_name("url")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpResponsePipeline.Phases")))
@interface ActualSyncKtor_client_coreHttpResponsePipelinePhases : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)phases __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_client_coreHttpResponsePipelinePhases *shared __attribute__((swift_name("shared")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *After __attribute__((swift_name("After")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *Parse __attribute__((swift_name("Parse")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *Receive __attribute__((swift_name("Receive")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *State __attribute__((swift_name("State")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *Transform __attribute__((swift_name("Transform")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpResponseContainer")))
@interface ActualSyncKtor_client_coreHttpResponseContainer : ActualSyncBase
- (instancetype)initWithExpectedType:(ActualSyncKtor_utilsTypeInfo *)expectedType response:(id)response __attribute__((swift_name("init(expectedType:response:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncKtor_client_coreHttpResponseContainer *)doCopyExpectedType:(ActualSyncKtor_utilsTypeInfo *)expectedType response:(id)response __attribute__((swift_name("doCopy(expectedType:response:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) ActualSyncKtor_utilsTypeInfo *expectedType __attribute__((swift_name("expectedType")));
@property (readonly) id response __attribute__((swift_name("response")));
@end

__attribute__((swift_name("Ktor_client_coreHttpClientCall")))
@interface ActualSyncKtor_client_coreHttpClientCall : ActualSyncBase <ActualSyncKotlinx_coroutines_coreCoroutineScope>
- (instancetype)initWithClient:(ActualSyncKtor_client_coreHttpClient *)client __attribute__((swift_name("init(client:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithClient:(ActualSyncKtor_client_coreHttpClient *)client requestData:(ActualSyncKtor_client_coreHttpRequestData *)requestData responseData:(ActualSyncKtor_client_coreHttpResponseData *)responseData __attribute__((swift_name("init(client:requestData:responseData:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncKtor_client_coreHttpClientCallCompanion *companion __attribute__((swift_name("companion")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)bodyInfo:(ActualSyncKtor_utilsTypeInfo *)info completionHandler:(void (^)(id _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("body(info:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)bodyNullableInfo:(ActualSyncKtor_utilsTypeInfo *)info completionHandler:(void (^)(id _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("bodyNullable(info:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (void)getResponseContentWithCompletionHandler:(void (^)(id<ActualSyncKtor_ioByteReadChannel> _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("getResponseContent(completionHandler:)")));
- (NSString *)description __attribute__((swift_name("description()")));

/**
 * @note This property has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
@property (readonly) BOOL allowDoubleReceive __attribute__((swift_name("allowDoubleReceive")));
@property (readonly) id<ActualSyncKtor_utilsAttributes> attributes __attribute__((swift_name("attributes")));
@property (readonly) ActualSyncKtor_client_coreHttpClient *client __attribute__((swift_name("client")));
@property (readonly) id<ActualSyncKotlinCoroutineContext> coroutineContext __attribute__((swift_name("coroutineContext")));
@property id<ActualSyncKtor_client_coreHttpRequest> request __attribute__((swift_name("request")));
@property ActualSyncKtor_client_coreHttpResponse *response __attribute__((swift_name("response")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpSendPipeline.Phases")))
@interface ActualSyncKtor_client_coreHttpSendPipelinePhases : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)phases __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_client_coreHttpSendPipelinePhases *shared __attribute__((swift_name("shared")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *Before __attribute__((swift_name("Before")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *Engine __attribute__((swift_name("Engine")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *Monitoring __attribute__((swift_name("Monitoring")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *Receive __attribute__((swift_name("Receive")));
@property (readonly) ActualSyncKtor_utilsPipelinePhase *State __attribute__((swift_name("State")));
@end


/**
 * @note annotations
 *   kotlinx.serialization.ExperimentalSerializationApi
*/
__attribute__((swift_name("Kotlinx_serialization_coreSerializersModuleCollector")))
@protocol ActualSyncKotlinx_serialization_coreSerializersModuleCollector
@required
- (void)contextualKClass:(id<ActualSyncKotlinKClass>)kClass provider:(id<ActualSyncKotlinx_serialization_coreKSerializer> (^)(NSArray<id<ActualSyncKotlinx_serialization_coreKSerializer>> *))provider __attribute__((swift_name("contextual(kClass:provider:)")));
- (void)contextualKClass:(id<ActualSyncKotlinKClass>)kClass serializer:(id<ActualSyncKotlinx_serialization_coreKSerializer>)serializer __attribute__((swift_name("contextual(kClass:serializer:)")));
- (void)polymorphicBaseClass:(id<ActualSyncKotlinKClass>)baseClass actualClass:(id<ActualSyncKotlinKClass>)actualClass actualSerializer:(id<ActualSyncKotlinx_serialization_coreKSerializer>)actualSerializer __attribute__((swift_name("polymorphic(baseClass:actualClass:actualSerializer:)")));
- (void)polymorphicDefaultBaseClass:(id<ActualSyncKotlinKClass>)baseClass defaultDeserializerProvider:(id<ActualSyncKotlinx_serialization_coreDeserializationStrategy> _Nullable (^)(NSString * _Nullable))defaultDeserializerProvider __attribute__((swift_name("polymorphicDefault(baseClass:defaultDeserializerProvider:)"))) __attribute__((deprecated("Deprecated in favor of function with more precise name: polymorphicDefaultDeserializer")));
- (void)polymorphicDefaultDeserializerBaseClass:(id<ActualSyncKotlinKClass>)baseClass defaultDeserializerProvider:(id<ActualSyncKotlinx_serialization_coreDeserializationStrategy> _Nullable (^)(NSString * _Nullable))defaultDeserializerProvider __attribute__((swift_name("polymorphicDefaultDeserializer(baseClass:defaultDeserializerProvider:)")));
- (void)polymorphicDefaultSerializerBaseClass:(id<ActualSyncKotlinKClass>)baseClass defaultSerializerProvider:(id<ActualSyncKotlinx_serialization_coreSerializationStrategy> _Nullable (^)(id))defaultSerializerProvider __attribute__((swift_name("polymorphicDefaultSerializer(baseClass:defaultSerializerProvider:)")));
@end

__attribute__((swift_name("KotlinKDeclarationContainer")))
@protocol ActualSyncKotlinKDeclarationContainer
@required
@end

__attribute__((swift_name("KotlinKAnnotatedElement")))
@protocol ActualSyncKotlinKAnnotatedElement
@required
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
__attribute__((swift_name("KotlinKClassifier")))
@protocol ActualSyncKotlinKClassifier
@required
@end

__attribute__((swift_name("KotlinKClass")))
@protocol ActualSyncKotlinKClass <ActualSyncKotlinKDeclarationContainer, ActualSyncKotlinKAnnotatedElement, ActualSyncKotlinKClassifier>
@required

/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
- (BOOL)isInstanceValue:(id _Nullable)value __attribute__((swift_name("isInstance(value:)")));
@property (readonly) NSString * _Nullable qualifiedName __attribute__((swift_name("qualifiedName")));
@property (readonly) NSString * _Nullable simpleName __attribute__((swift_name("simpleName")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpUrl")))
@interface ActualSyncKtor_httpUrl : ActualSyncBase
@property (class, readonly, getter=companion) ActualSyncKtor_httpUrlCompanion *companion __attribute__((swift_name("companion")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *encodedFragment __attribute__((swift_name("encodedFragment")));
@property (readonly) NSString * _Nullable encodedPassword __attribute__((swift_name("encodedPassword")));
@property (readonly) NSString *encodedPath __attribute__((swift_name("encodedPath")));
@property (readonly) NSString *encodedPathAndQuery __attribute__((swift_name("encodedPathAndQuery")));
@property (readonly) NSString *encodedQuery __attribute__((swift_name("encodedQuery")));
@property (readonly) NSString * _Nullable encodedUser __attribute__((swift_name("encodedUser")));
@property (readonly) NSString *fragment __attribute__((swift_name("fragment")));
@property (readonly) NSString *host __attribute__((swift_name("host")));
@property (readonly) id<ActualSyncKtor_httpParameters> parameters __attribute__((swift_name("parameters")));
@property (readonly) NSString * _Nullable password __attribute__((swift_name("password")));
@property (readonly) NSArray<NSString *> *pathSegments __attribute__((swift_name("pathSegments"))) __attribute__((deprecated("\n        `pathSegments` is deprecated.\n\n        This property will contain an empty path segment at the beginning for URLs with a hostname,\n        and an empty path segment at the end for the URLs with a trailing slash. If you need to keep this behaviour please\n        use [rawSegments]. If you only need to access the meaningful parts of the path, consider using [segments] instead.\n             \n        Please decide if you need [rawSegments] or [segments] explicitly.\n        ")));
@property (readonly) int32_t port __attribute__((swift_name("port")));
@property (readonly) ActualSyncKtor_httpURLProtocol *protocol __attribute__((swift_name("protocol")));
@property (readonly) ActualSyncKtor_httpURLProtocol * _Nullable protocolOrNull __attribute__((swift_name("protocolOrNull")));
@property (readonly) NSArray<NSString *> *rawSegments __attribute__((swift_name("rawSegments")));
@property (readonly) NSArray<NSString *> *segments __attribute__((swift_name("segments")));
@property (readonly) int32_t specifiedPort __attribute__((swift_name("specifiedPort")));
@property (readonly) BOOL trailingQuery __attribute__((swift_name("trailingQuery")));
@property (readonly) NSString * _Nullable user __attribute__((swift_name("user")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpHttpMethod")))
@interface ActualSyncKtor_httpHttpMethod : ActualSyncBase
- (instancetype)initWithValue:(NSString *)value __attribute__((swift_name("init(value:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncKtor_httpHttpMethodCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncKtor_httpHttpMethod *)doCopyValue:(NSString *)value __attribute__((swift_name("doCopy(value:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *value __attribute__((swift_name("value")));
@end

__attribute__((swift_name("Ktor_utilsStringValues")))
@protocol ActualSyncKtor_utilsStringValues
@required
- (BOOL)containsName:(NSString *)name __attribute__((swift_name("contains(name:)")));
- (BOOL)containsName:(NSString *)name value:(NSString *)value __attribute__((swift_name("contains(name:value:)")));
- (NSSet<id<ActualSyncKotlinMapEntry>> *)entries __attribute__((swift_name("entries()")));
- (void)forEachBody:(void (^)(NSString *, NSArray<NSString *> *))body __attribute__((swift_name("forEach(body:)")));
- (NSString * _Nullable)getName:(NSString *)name __attribute__((swift_name("get(name:)")));
- (NSArray<NSString *> * _Nullable)getAllName:(NSString *)name __attribute__((swift_name("getAll(name:)")));
- (BOOL)isEmpty_ __attribute__((swift_name("isEmpty()")));
- (NSSet<NSString *> *)names __attribute__((swift_name("names()")));
@property (readonly) BOOL caseInsensitiveName __attribute__((swift_name("caseInsensitiveName")));
@end

__attribute__((swift_name("Ktor_httpHeaders")))
@protocol ActualSyncKtor_httpHeaders <ActualSyncKtor_utilsStringValues>
@required
@end

__attribute__((swift_name("Ktor_httpOutgoingContent")))
@interface ActualSyncKtor_httpOutgoingContent : ActualSyncBase
- (id _Nullable)getPropertyKey:(ActualSyncKtor_utilsAttributeKey<id> *)key __attribute__((swift_name("getProperty(key:)")));
- (void)setPropertyKey:(ActualSyncKtor_utilsAttributeKey<id> *)key value:(id _Nullable)value __attribute__((swift_name("setProperty(key:value:)")));
- (id<ActualSyncKtor_httpHeaders> _Nullable)trailers __attribute__((swift_name("trailers()")));
@property (readonly) ActualSyncLong * _Nullable contentLength __attribute__((swift_name("contentLength")));
@property (readonly) ActualSyncKtor_httpContentType * _Nullable contentType __attribute__((swift_name("contentType")));
@property (readonly) id<ActualSyncKtor_httpHeaders> headers __attribute__((swift_name("headers")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode * _Nullable status __attribute__((swift_name("status")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreJob")))
@protocol ActualSyncKotlinx_coroutines_coreJob <ActualSyncKotlinCoroutineContextElement>
@required

/**
 * @note annotations
 *   kotlinx.coroutines.InternalCoroutinesApi
*/
- (id<ActualSyncKotlinx_coroutines_coreChildHandle>)attachChildChild:(id<ActualSyncKotlinx_coroutines_coreChildJob>)child __attribute__((swift_name("attachChild(child:)")));
- (void)cancelCause:(ActualSyncKotlinCancellationException * _Nullable)cause __attribute__((swift_name("cancel(cause:)")));

/**
 * @note annotations
 *   kotlinx.coroutines.InternalCoroutinesApi
*/
- (ActualSyncKotlinCancellationException *)getCancellationException __attribute__((swift_name("getCancellationException()")));
- (id<ActualSyncKotlinx_coroutines_coreDisposableHandle>)invokeOnCompletionHandler:(void (^)(ActualSyncKotlinThrowable * _Nullable))handler __attribute__((swift_name("invokeOnCompletion(handler:)")));

/**
 * @note annotations
 *   kotlinx.coroutines.InternalCoroutinesApi
*/
- (id<ActualSyncKotlinx_coroutines_coreDisposableHandle>)invokeOnCompletionOnCancelling:(BOOL)onCancelling invokeImmediately:(BOOL)invokeImmediately handler:(void (^)(ActualSyncKotlinThrowable * _Nullable))handler __attribute__((swift_name("invokeOnCompletion(onCancelling:invokeImmediately:handler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)joinWithCompletionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("join(completionHandler:)")));
- (id<ActualSyncKotlinx_coroutines_coreJob>)plusOther_:(id<ActualSyncKotlinx_coroutines_coreJob>)other __attribute__((swift_name("plus(other_:)"))) __attribute__((unavailable("Operator '+' on two Job objects is meaningless. Job is a coroutine context element and `+` is a set-sum operator for coroutine contexts. The job to the right of `+` just replaces the job the left of `+`.")));
- (BOOL)start __attribute__((swift_name("start()")));
@property (readonly) id<ActualSyncKotlinSequence> children __attribute__((swift_name("children")));
@property (readonly) BOOL isActive __attribute__((swift_name("isActive")));
@property (readonly) BOOL isCancelled __attribute__((swift_name("isCancelled")));
@property (readonly) BOOL isCompleted __attribute__((swift_name("isCompleted")));
@property (readonly) id<ActualSyncKotlinx_coroutines_coreSelectClause0> onJoin __attribute__((swift_name("onJoin")));

/**
 * @note annotations
 *   kotlinx.coroutines.ExperimentalCoroutinesApi
*/
@property (readonly) id<ActualSyncKotlinx_coroutines_coreJob> _Nullable parent __attribute__((swift_name("parent")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpHttpStatusCode")))
@interface ActualSyncKtor_httpHttpStatusCode : ActualSyncBase <ActualSyncKotlinComparable>
- (instancetype)initWithValue:(int32_t)value description:(NSString *)description __attribute__((swift_name("init(value:description:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncKtor_httpHttpStatusCodeCompanion *companion __attribute__((swift_name("companion")));
- (int32_t)compareToOther:(ActualSyncKtor_httpHttpStatusCode *)other __attribute__((swift_name("compareTo(other:)")));
- (ActualSyncKtor_httpHttpStatusCode *)doCopyValue:(int32_t)value description:(NSString *)description __attribute__((swift_name("doCopy(value:description:)")));
- (ActualSyncKtor_httpHttpStatusCode *)descriptionValue:(NSString *)value __attribute__((swift_name("description(value:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *description_ __attribute__((swift_name("description_")));
@property (readonly) int32_t value __attribute__((swift_name("value")));
@end


/**
 * @note annotations
 *   kotlinx.serialization.Serializable
*/
__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_utilsGMTDate")))
@interface ActualSyncKtor_utilsGMTDate : ActualSyncBase <ActualSyncKotlinComparable>
- (instancetype)initWithSeconds:(int32_t)seconds minutes:(int32_t)minutes hours:(int32_t)hours dayOfWeek:(ActualSyncKtor_utilsWeekDay *)dayOfWeek dayOfMonth:(int32_t)dayOfMonth dayOfYear:(int32_t)dayOfYear month:(ActualSyncKtor_utilsMonth *)month year:(int32_t)year timestamp:(int64_t)timestamp __attribute__((swift_name("init(seconds:minutes:hours:dayOfWeek:dayOfMonth:dayOfYear:month:year:timestamp:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncKtor_utilsGMTDateCompanion *companion __attribute__((swift_name("companion")));
- (int32_t)compareToOther:(ActualSyncKtor_utilsGMTDate *)other __attribute__((swift_name("compareTo(other:)")));
- (ActualSyncKtor_utilsGMTDate *)doCopy __attribute__((swift_name("doCopy()")));
- (ActualSyncKtor_utilsGMTDate *)doCopySeconds:(int32_t)seconds minutes:(int32_t)minutes hours:(int32_t)hours dayOfWeek:(ActualSyncKtor_utilsWeekDay *)dayOfWeek dayOfMonth:(int32_t)dayOfMonth dayOfYear:(int32_t)dayOfYear month:(ActualSyncKtor_utilsMonth *)month year:(int32_t)year timestamp:(int64_t)timestamp __attribute__((swift_name("doCopy(seconds:minutes:hours:dayOfWeek:dayOfMonth:dayOfYear:month:year:timestamp:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int32_t dayOfMonth __attribute__((swift_name("dayOfMonth")));
@property (readonly) ActualSyncKtor_utilsWeekDay *dayOfWeek __attribute__((swift_name("dayOfWeek")));
@property (readonly) int32_t dayOfYear __attribute__((swift_name("dayOfYear")));
@property (readonly) int32_t hours __attribute__((swift_name("hours")));
@property (readonly) int32_t minutes __attribute__((swift_name("minutes")));
@property (readonly) ActualSyncKtor_utilsMonth *month __attribute__((swift_name("month")));
@property (readonly) int32_t seconds __attribute__((swift_name("seconds")));
@property (readonly) int64_t timestamp __attribute__((swift_name("timestamp")));
@property (readonly) int32_t year __attribute__((swift_name("year")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpHttpProtocolVersion")))
@interface ActualSyncKtor_httpHttpProtocolVersion : ActualSyncBase
- (instancetype)initWithName:(NSString *)name major:(int32_t)major minor:(int32_t)minor __attribute__((swift_name("init(name:major:minor:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncKtor_httpHttpProtocolVersionCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncKtor_httpHttpProtocolVersion *)doCopyName:(NSString *)name major:(int32_t)major minor:(int32_t)minor __attribute__((swift_name("doCopy(name:major:minor:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int32_t major __attribute__((swift_name("major")));
@property (readonly) int32_t minor __attribute__((swift_name("minor")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.3")
*/
__attribute__((swift_name("KotlinContinuation")))
@protocol ActualSyncKotlinContinuation
@required
- (void)resumeWithResult:(id _Nullable)result __attribute__((swift_name("resumeWith(result:)")));
@property (readonly) id<ActualSyncKotlinCoroutineContext> context __attribute__((swift_name("context")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.3")
 *   kotlin.ExperimentalStdlibApi
*/
__attribute__((swift_name("KotlinAbstractCoroutineContextKey")))
@interface ActualSyncKotlinAbstractCoroutineContextKey<B, E> : ActualSyncBase <ActualSyncKotlinCoroutineContextKey>
- (instancetype)initWithBaseKey:(id<ActualSyncKotlinCoroutineContextKey>)baseKey safeCast:(E _Nullable (^)(id<ActualSyncKotlinCoroutineContextElement>))safeCast __attribute__((swift_name("init(baseKey:safeCast:)"))) __attribute__((objc_designated_initializer));
@end


/**
 * @note annotations
 *   kotlin.ExperimentalStdlibApi
*/
__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Kotlinx_coroutines_coreCoroutineDispatcher.Key")))
@interface ActualSyncKotlinx_coroutines_coreCoroutineDispatcherKey : ActualSyncKotlinAbstractCoroutineContextKey<id<ActualSyncKotlinContinuationInterceptor>, ActualSyncKotlinx_coroutines_coreCoroutineDispatcher *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithBaseKey:(id<ActualSyncKotlinCoroutineContextKey>)baseKey safeCast:(id<ActualSyncKotlinCoroutineContextElement> _Nullable (^)(id<ActualSyncKotlinCoroutineContextElement>))safeCast __attribute__((swift_name("init(baseKey:safeCast:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)key __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKotlinx_coroutines_coreCoroutineDispatcherKey *shared __attribute__((swift_name("shared")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreRunnable")))
@protocol ActualSyncKotlinx_coroutines_coreRunnable
@required
- (void)run __attribute__((swift_name("run()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_utilsTypeInfo")))
@interface ActualSyncKtor_utilsTypeInfo : ActualSyncBase
- (instancetype)initWithType:(id<ActualSyncKotlinKClass>)type kotlinType:(id<ActualSyncKotlinKType> _Nullable)kotlinType __attribute__((swift_name("init(type:kotlinType:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithType:(id<ActualSyncKotlinKClass>)type reifiedType:(id<ActualSyncKotlinKType>)reifiedType kotlinType:(id<ActualSyncKotlinKType> _Nullable)kotlinType __attribute__((swift_name("init(type:reifiedType:kotlinType:)"))) __attribute__((objc_designated_initializer)) __attribute__((deprecated("Use constructor without reifiedType parameter.")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) id<ActualSyncKotlinKType> _Nullable kotlinType __attribute__((swift_name("kotlinType")));
@property (readonly) id<ActualSyncKotlinKClass> type __attribute__((swift_name("type")));
@end

__attribute__((swift_name("Ktor_ioByteReadChannel")))
@protocol ActualSyncKtor_ioByteReadChannel
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)awaitContentMin:(int32_t)min completionHandler:(void (^)(ActualSyncBoolean * _Nullable, NSError * _Nullable))completionHandler __attribute__((swift_name("awaitContent(min:completionHandler:)")));
- (void)cancelCause_:(ActualSyncKotlinThrowable * _Nullable)cause __attribute__((swift_name("cancel(cause_:)")));
@property (readonly) ActualSyncKotlinThrowable * _Nullable closedCause __attribute__((swift_name("closedCause")));
@property (readonly) BOOL isClosedForRead __attribute__((swift_name("isClosedForRead")));
@property (readonly) id<ActualSyncKotlinx_io_coreSource> readBuffer __attribute__((swift_name("readBuffer")));
@end

__attribute__((swift_name("Ktor_utilsStringValuesBuilder")))
@protocol ActualSyncKtor_utilsStringValuesBuilder
@required
- (void)appendName:(NSString *)name value:(NSString *)value __attribute__((swift_name("append(name:value:)")));
- (void)appendAllStringValues:(id<ActualSyncKtor_utilsStringValues>)stringValues __attribute__((swift_name("appendAll(stringValues:)")));
- (void)appendAllName:(NSString *)name values:(id)values __attribute__((swift_name("appendAll(name:values:)")));
- (void)appendMissingStringValues:(id<ActualSyncKtor_utilsStringValues>)stringValues __attribute__((swift_name("appendMissing(stringValues:)")));
- (void)appendMissingName:(NSString *)name values:(id)values __attribute__((swift_name("appendMissing(name:values:)")));
- (id<ActualSyncKtor_utilsStringValues>)build __attribute__((swift_name("build()")));
- (void)clear __attribute__((swift_name("clear()")));
- (BOOL)containsName:(NSString *)name __attribute__((swift_name("contains(name:)")));
- (BOOL)containsName:(NSString *)name value:(NSString *)value __attribute__((swift_name("contains(name:value:)")));
- (NSSet<id<ActualSyncKotlinMapEntry>> *)entries __attribute__((swift_name("entries()")));
- (NSString * _Nullable)getName:(NSString *)name __attribute__((swift_name("get(name:)")));
- (NSArray<NSString *> * _Nullable)getAllName:(NSString *)name __attribute__((swift_name("getAll(name:)")));
- (BOOL)isEmpty_ __attribute__((swift_name("isEmpty()")));
- (NSSet<NSString *> *)names __attribute__((swift_name("names()")));
- (void)removeName:(NSString *)name __attribute__((swift_name("remove(name:)")));
- (BOOL)removeName:(NSString *)name value:(NSString *)value __attribute__((swift_name("remove(name:value:)")));
- (void)removeKeysWithNoEntries __attribute__((swift_name("removeKeysWithNoEntries()")));
- (void)setName:(NSString *)name value:(NSString *)value __attribute__((swift_name("set(name:value:)")));
@property (readonly) BOOL caseInsensitiveName __attribute__((swift_name("caseInsensitiveName")));
@end

__attribute__((swift_name("Ktor_utilsStringValuesBuilderImpl")))
@interface ActualSyncKtor_utilsStringValuesBuilderImpl : ActualSyncBase <ActualSyncKtor_utilsStringValuesBuilder>
- (instancetype)initWithCaseInsensitiveName:(BOOL)caseInsensitiveName size:(int32_t)size __attribute__((swift_name("init(caseInsensitiveName:size:)"))) __attribute__((objc_designated_initializer));
- (void)appendName:(NSString *)name value:(NSString *)value __attribute__((swift_name("append(name:value:)")));
- (void)appendAllStringValues:(id<ActualSyncKtor_utilsStringValues>)stringValues __attribute__((swift_name("appendAll(stringValues:)")));
- (void)appendAllName:(NSString *)name values:(id)values __attribute__((swift_name("appendAll(name:values:)")));
- (void)appendMissingStringValues:(id<ActualSyncKtor_utilsStringValues>)stringValues __attribute__((swift_name("appendMissing(stringValues:)")));
- (void)appendMissingName:(NSString *)name values:(id)values __attribute__((swift_name("appendMissing(name:values:)")));
- (id<ActualSyncKtor_utilsStringValues>)build __attribute__((swift_name("build()")));
- (void)clear __attribute__((swift_name("clear()")));
- (BOOL)containsName:(NSString *)name __attribute__((swift_name("contains(name:)")));
- (BOOL)containsName:(NSString *)name value:(NSString *)value __attribute__((swift_name("contains(name:value:)")));
- (NSSet<id<ActualSyncKotlinMapEntry>> *)entries __attribute__((swift_name("entries()")));
- (NSString * _Nullable)getName:(NSString *)name __attribute__((swift_name("get(name:)")));
- (NSArray<NSString *> * _Nullable)getAllName:(NSString *)name __attribute__((swift_name("getAll(name:)")));
- (BOOL)isEmpty_ __attribute__((swift_name("isEmpty()")));
- (NSSet<NSString *> *)names __attribute__((swift_name("names()")));
- (void)removeName:(NSString *)name __attribute__((swift_name("remove(name:)")));
- (BOOL)removeName:(NSString *)name value:(NSString *)value __attribute__((swift_name("remove(name:value:)")));
- (void)removeKeysWithNoEntries __attribute__((swift_name("removeKeysWithNoEntries()")));
- (void)setName:(NSString *)name value:(NSString *)value __attribute__((swift_name("set(name:value:)")));

/**
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (void)validateNameName:(NSString *)name __attribute__((swift_name("validateName(name:)")));

/**
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (void)validateValueValue:(NSString *)value __attribute__((swift_name("validateValue(value:)")));
@property (readonly) BOOL caseInsensitiveName __attribute__((swift_name("caseInsensitiveName")));

/**
 * @note This property has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
@property (readonly) ActualSyncMutableDictionary<NSString *, NSMutableArray<NSString *> *> *values __attribute__((swift_name("values")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpHeadersBuilder")))
@interface ActualSyncKtor_httpHeadersBuilder : ActualSyncKtor_utilsStringValuesBuilderImpl
- (instancetype)initWithSize:(int32_t)size __attribute__((swift_name("init(size:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCaseInsensitiveName:(BOOL)caseInsensitiveName size:(int32_t)size __attribute__((swift_name("init(caseInsensitiveName:size:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (id<ActualSyncKtor_httpHeaders>)build __attribute__((swift_name("build()")));

/**
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (void)validateNameName:(NSString *)name __attribute__((swift_name("validateName(name:)")));

/**
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (void)validateValueValue:(NSString *)value __attribute__((swift_name("validateValue(value:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpRequestBuilder.Companion")))
@interface ActualSyncKtor_client_coreHttpRequestBuilderCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_client_coreHttpRequestBuilderCompanion *shared __attribute__((swift_name("shared")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpURLBuilder")))
@interface ActualSyncKtor_httpURLBuilder : ActualSyncBase
- (instancetype)initWithProtocol:(ActualSyncKtor_httpURLProtocol * _Nullable)protocol host:(NSString *)host port:(int32_t)port user:(NSString * _Nullable)user password:(NSString * _Nullable)password pathSegments:(NSArray<NSString *> *)pathSegments parameters:(id<ActualSyncKtor_httpParameters>)parameters fragment:(NSString *)fragment trailingQuery:(BOOL)trailingQuery __attribute__((swift_name("init(protocol:host:port:user:password:pathSegments:parameters:fragment:trailingQuery:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncKtor_httpURLBuilderCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncKtor_httpUrl *)build __attribute__((swift_name("build()")));
- (NSString *)buildString __attribute__((swift_name("buildString()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property NSString *encodedFragment __attribute__((swift_name("encodedFragment")));
@property id<ActualSyncKtor_httpParametersBuilder> encodedParameters __attribute__((swift_name("encodedParameters")));
@property NSString * _Nullable encodedPassword __attribute__((swift_name("encodedPassword")));
@property NSArray<NSString *> *encodedPathSegments __attribute__((swift_name("encodedPathSegments")));
@property NSString * _Nullable encodedUser __attribute__((swift_name("encodedUser")));
@property NSString *fragment __attribute__((swift_name("fragment")));
@property NSString *host __attribute__((swift_name("host")));
@property (readonly) id<ActualSyncKtor_httpParametersBuilder> parameters __attribute__((swift_name("parameters")));
@property NSString * _Nullable password __attribute__((swift_name("password")));
@property NSArray<NSString *> *pathSegments __attribute__((swift_name("pathSegments")));
@property int32_t port __attribute__((swift_name("port")));
@property ActualSyncKtor_httpURLProtocol *protocol __attribute__((swift_name("protocol")));
@property ActualSyncKtor_httpURLProtocol * _Nullable protocolOrNull __attribute__((swift_name("protocolOrNull")));
@property BOOL trailingQuery __attribute__((swift_name("trailingQuery")));
@property NSString * _Nullable user __attribute__((swift_name("user")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_client_coreHttpClientCall.Companion")))
@interface ActualSyncKtor_client_coreHttpClientCallCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_client_coreHttpClientCallCompanion *shared __attribute__((swift_name("shared")));
@end

__attribute__((swift_name("Ktor_client_coreHttpRequest")))
@protocol ActualSyncKtor_client_coreHttpRequest <ActualSyncKtor_httpHttpMessage, ActualSyncKotlinx_coroutines_coreCoroutineScope>
@required
@property (readonly) id<ActualSyncKtor_utilsAttributes> attributes __attribute__((swift_name("attributes")));
@property (readonly) ActualSyncKtor_client_coreHttpClientCall *call __attribute__((swift_name("call")));
@property (readonly) ActualSyncKtor_httpOutgoingContent *content __attribute__((swift_name("content")));
@property (readonly) ActualSyncKtor_httpHttpMethod *method __attribute__((swift_name("method")));
@property (readonly) ActualSyncKtor_httpUrl *url __attribute__((swift_name("url")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpUrl.Companion")))
@interface ActualSyncKtor_httpUrlCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_httpUrlCompanion *shared __attribute__((swift_name("shared")));
@end

__attribute__((swift_name("Ktor_httpParameters")))
@protocol ActualSyncKtor_httpParameters <ActualSyncKtor_utilsStringValues>
@required
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpURLProtocol")))
@interface ActualSyncKtor_httpURLProtocol : ActualSyncBase
- (instancetype)initWithName:(NSString *)name defaultPort:(int32_t)defaultPort __attribute__((swift_name("init(name:defaultPort:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncKtor_httpURLProtocolCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncKtor_httpURLProtocol *)doCopyName:(NSString *)name defaultPort:(int32_t)defaultPort __attribute__((swift_name("doCopy(name:defaultPort:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int32_t defaultPort __attribute__((swift_name("defaultPort")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpHttpMethod.Companion")))
@interface ActualSyncKtor_httpHttpMethodCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_httpHttpMethodCompanion *shared __attribute__((swift_name("shared")));
- (ActualSyncKtor_httpHttpMethod *)parseMethod:(NSString *)method __attribute__((swift_name("parse(method:)")));
@property (readonly) NSArray<ActualSyncKtor_httpHttpMethod *> *DefaultMethods __attribute__((swift_name("DefaultMethods")));
@property (readonly) ActualSyncKtor_httpHttpMethod *Delete __attribute__((swift_name("Delete")));
@property (readonly) ActualSyncKtor_httpHttpMethod *Get __attribute__((swift_name("Get")));
@property (readonly) ActualSyncKtor_httpHttpMethod *Head __attribute__((swift_name("Head")));
@property (readonly) ActualSyncKtor_httpHttpMethod *Options __attribute__((swift_name("Options")));
@property (readonly) ActualSyncKtor_httpHttpMethod *Patch __attribute__((swift_name("Patch")));
@property (readonly) ActualSyncKtor_httpHttpMethod *Post __attribute__((swift_name("Post")));
@property (readonly) ActualSyncKtor_httpHttpMethod *Put __attribute__((swift_name("Put")));
@end

__attribute__((swift_name("KotlinMapEntry")))
@protocol ActualSyncKotlinMapEntry
@required
@property (readonly) id _Nullable key __attribute__((swift_name("key")));
@property (readonly) id _Nullable value __attribute__((swift_name("value")));
@end

__attribute__((swift_name("Ktor_httpHeaderValueWithParameters")))
@interface ActualSyncKtor_httpHeaderValueWithParameters : ActualSyncBase
- (instancetype)initWithContent:(NSString *)content parameters:(NSArray<ActualSyncKtor_httpHeaderValueParam *> *)parameters __attribute__((swift_name("init(content:parameters:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncKtor_httpHeaderValueWithParametersCompanion *companion __attribute__((swift_name("companion")));
- (NSString * _Nullable)parameterName:(NSString *)name __attribute__((swift_name("parameter(name:)")));
- (NSString *)description __attribute__((swift_name("description()")));

/**
 * @note This property has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
@property (readonly) NSString *content __attribute__((swift_name("content")));
@property (readonly) NSArray<ActualSyncKtor_httpHeaderValueParam *> *parameters __attribute__((swift_name("parameters")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpContentType")))
@interface ActualSyncKtor_httpContentType : ActualSyncKtor_httpHeaderValueWithParameters
- (instancetype)initWithContentType:(NSString *)contentType contentSubtype:(NSString *)contentSubtype parameters:(NSArray<ActualSyncKtor_httpHeaderValueParam *> *)parameters __attribute__((swift_name("init(contentType:contentSubtype:parameters:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithContent:(NSString *)content parameters:(NSArray<ActualSyncKtor_httpHeaderValueParam *> *)parameters __attribute__((swift_name("init(content:parameters:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly, getter=companion) ActualSyncKtor_httpContentTypeCompanion *companion __attribute__((swift_name("companion")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (BOOL)matchPattern:(ActualSyncKtor_httpContentType *)pattern __attribute__((swift_name("match(pattern:)")));
- (BOOL)matchPattern_:(NSString *)pattern __attribute__((swift_name("match(pattern_:)")));
- (ActualSyncKtor_httpContentType *)withParameterName:(NSString *)name value:(NSString *)value __attribute__((swift_name("withParameter(name:value:)")));
- (ActualSyncKtor_httpContentType *)withoutParameters __attribute__((swift_name("withoutParameters()")));
@property (readonly) NSString *contentSubtype __attribute__((swift_name("contentSubtype")));
@property (readonly) NSString *contentType __attribute__((swift_name("contentType")));
@end


/**
 * @note annotations
 *   kotlinx.coroutines.InternalCoroutinesApi
*/
__attribute__((swift_name("Kotlinx_coroutines_coreChildHandle")))
@protocol ActualSyncKotlinx_coroutines_coreChildHandle <ActualSyncKotlinx_coroutines_coreDisposableHandle>
@required

/**
 * @note annotations
 *   kotlinx.coroutines.InternalCoroutinesApi
*/
- (BOOL)childCancelledCause:(ActualSyncKotlinThrowable *)cause __attribute__((swift_name("childCancelled(cause:)")));

/**
 * @note annotations
 *   kotlinx.coroutines.InternalCoroutinesApi
*/
@property (readonly) id<ActualSyncKotlinx_coroutines_coreJob> _Nullable parent __attribute__((swift_name("parent")));
@end


/**
 * @note annotations
 *   kotlinx.coroutines.InternalCoroutinesApi
*/
__attribute__((swift_name("Kotlinx_coroutines_coreChildJob")))
@protocol ActualSyncKotlinx_coroutines_coreChildJob <ActualSyncKotlinx_coroutines_coreJob>
@required

/**
 * @note annotations
 *   kotlinx.coroutines.InternalCoroutinesApi
*/
- (void)parentCancelledParentJob:(id<ActualSyncKotlinx_coroutines_coreParentJob>)parentJob __attribute__((swift_name("parentCancelled(parentJob:)")));
@end

__attribute__((swift_name("KotlinSequence")))
@protocol ActualSyncKotlinSequence
@required
- (id<ActualSyncKotlinIterator>)iterator __attribute__((swift_name("iterator()")));
@end


/**
 * @note annotations
 *   kotlinx.coroutines.InternalCoroutinesApi
*/
__attribute__((swift_name("Kotlinx_coroutines_coreSelectClause")))
@protocol ActualSyncKotlinx_coroutines_coreSelectClause
@required
@property (readonly) id clauseObject __attribute__((swift_name("clauseObject")));
@property (readonly) ActualSyncKotlinUnit *(^(^ _Nullable onCancellationConstructor)(id<ActualSyncKotlinx_coroutines_coreSelectInstance>, id _Nullable, id _Nullable))(ActualSyncKotlinThrowable *, id _Nullable, id<ActualSyncKotlinCoroutineContext>) __attribute__((swift_name("onCancellationConstructor")));
@property (readonly) id _Nullable (^processResFunc)(id, id _Nullable, id _Nullable) __attribute__((swift_name("processResFunc")));
@property (readonly) void (^regFunc)(id, id<ActualSyncKotlinx_coroutines_coreSelectInstance>, id _Nullable) __attribute__((swift_name("regFunc")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreSelectClause0")))
@protocol ActualSyncKotlinx_coroutines_coreSelectClause0 <ActualSyncKotlinx_coroutines_coreSelectClause>
@required
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpHttpStatusCode.Companion")))
@interface ActualSyncKtor_httpHttpStatusCodeCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_httpHttpStatusCodeCompanion *shared __attribute__((swift_name("shared")));
- (ActualSyncKtor_httpHttpStatusCode *)fromValueValue:(int32_t)value __attribute__((swift_name("fromValue(value:)")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *Accepted __attribute__((swift_name("Accepted")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *BadGateway __attribute__((swift_name("BadGateway")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *BadRequest __attribute__((swift_name("BadRequest")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *Conflict __attribute__((swift_name("Conflict")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *Continue __attribute__((swift_name("Continue")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *Created __attribute__((swift_name("Created")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *ExpectationFailed __attribute__((swift_name("ExpectationFailed")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *FailedDependency __attribute__((swift_name("FailedDependency")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *Forbidden __attribute__((swift_name("Forbidden")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *Found __attribute__((swift_name("Found")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *GatewayTimeout __attribute__((swift_name("GatewayTimeout")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *Gone __attribute__((swift_name("Gone")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *InsufficientStorage __attribute__((swift_name("InsufficientStorage")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *InternalServerError __attribute__((swift_name("InternalServerError")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *LengthRequired __attribute__((swift_name("LengthRequired")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *Locked __attribute__((swift_name("Locked")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *MethodNotAllowed __attribute__((swift_name("MethodNotAllowed")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *MovedPermanently __attribute__((swift_name("MovedPermanently")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *MultiStatus __attribute__((swift_name("MultiStatus")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *MultipleChoices __attribute__((swift_name("MultipleChoices")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *NoContent __attribute__((swift_name("NoContent")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *NonAuthoritativeInformation __attribute__((swift_name("NonAuthoritativeInformation")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *NotAcceptable __attribute__((swift_name("NotAcceptable")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *NotFound __attribute__((swift_name("NotFound")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *NotImplemented __attribute__((swift_name("NotImplemented")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *NotModified __attribute__((swift_name("NotModified")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *OK __attribute__((swift_name("OK")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *PartialContent __attribute__((swift_name("PartialContent")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *PayloadTooLarge __attribute__((swift_name("PayloadTooLarge")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *PaymentRequired __attribute__((swift_name("PaymentRequired")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *PermanentRedirect __attribute__((swift_name("PermanentRedirect")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *PreconditionFailed __attribute__((swift_name("PreconditionFailed")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *Processing __attribute__((swift_name("Processing")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *ProxyAuthenticationRequired __attribute__((swift_name("ProxyAuthenticationRequired")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *RequestHeaderFieldTooLarge __attribute__((swift_name("RequestHeaderFieldTooLarge")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *RequestTimeout __attribute__((swift_name("RequestTimeout")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *RequestURITooLong __attribute__((swift_name("RequestURITooLong")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *RequestedRangeNotSatisfiable __attribute__((swift_name("RequestedRangeNotSatisfiable")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *ResetContent __attribute__((swift_name("ResetContent")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *SeeOther __attribute__((swift_name("SeeOther")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *ServiceUnavailable __attribute__((swift_name("ServiceUnavailable")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *SwitchProxy __attribute__((swift_name("SwitchProxy")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *SwitchingProtocols __attribute__((swift_name("SwitchingProtocols")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *TemporaryRedirect __attribute__((swift_name("TemporaryRedirect")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *TooEarly __attribute__((swift_name("TooEarly")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *TooManyRequests __attribute__((swift_name("TooManyRequests")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *Unauthorized __attribute__((swift_name("Unauthorized")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *UnprocessableEntity __attribute__((swift_name("UnprocessableEntity")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *UnsupportedMediaType __attribute__((swift_name("UnsupportedMediaType")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *UpgradeRequired __attribute__((swift_name("UpgradeRequired")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *UseProxy __attribute__((swift_name("UseProxy")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *VariantAlsoNegotiates __attribute__((swift_name("VariantAlsoNegotiates")));
@property (readonly) ActualSyncKtor_httpHttpStatusCode *VersionNotSupported __attribute__((swift_name("VersionNotSupported")));
@property (readonly) NSArray<ActualSyncKtor_httpHttpStatusCode *> *allStatusCodes __attribute__((swift_name("allStatusCodes")));
@end

__attribute__((swift_name("KotlinEnum")))
@interface ActualSyncKotlinEnum<E> : ActualSyncBase <ActualSyncKotlinComparable>
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncKotlinEnumCompanion *companion __attribute__((swift_name("companion")));
- (int32_t)compareToOther:(E)other __attribute__((swift_name("compareTo(other:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) int32_t ordinal __attribute__((swift_name("ordinal")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_utilsWeekDay")))
@interface ActualSyncKtor_utilsWeekDay : ActualSyncKotlinEnum<ActualSyncKtor_utilsWeekDay *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly, getter=companion) ActualSyncKtor_utilsWeekDayCompanion *companion __attribute__((swift_name("companion")));
@property (class, readonly) ActualSyncKtor_utilsWeekDay *monday __attribute__((swift_name("monday")));
@property (class, readonly) ActualSyncKtor_utilsWeekDay *tuesday __attribute__((swift_name("tuesday")));
@property (class, readonly) ActualSyncKtor_utilsWeekDay *wednesday __attribute__((swift_name("wednesday")));
@property (class, readonly) ActualSyncKtor_utilsWeekDay *thursday __attribute__((swift_name("thursday")));
@property (class, readonly) ActualSyncKtor_utilsWeekDay *friday __attribute__((swift_name("friday")));
@property (class, readonly) ActualSyncKtor_utilsWeekDay *saturday __attribute__((swift_name("saturday")));
@property (class, readonly) ActualSyncKtor_utilsWeekDay *sunday __attribute__((swift_name("sunday")));
+ (ActualSyncKotlinArray<ActualSyncKtor_utilsWeekDay *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<ActualSyncKtor_utilsWeekDay *> *entries __attribute__((swift_name("entries")));
@property (readonly) NSString *value __attribute__((swift_name("value")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_utilsMonth")))
@interface ActualSyncKtor_utilsMonth : ActualSyncKotlinEnum<ActualSyncKtor_utilsMonth *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly, getter=companion) ActualSyncKtor_utilsMonthCompanion *companion __attribute__((swift_name("companion")));
@property (class, readonly) ActualSyncKtor_utilsMonth *january __attribute__((swift_name("january")));
@property (class, readonly) ActualSyncKtor_utilsMonth *february __attribute__((swift_name("february")));
@property (class, readonly) ActualSyncKtor_utilsMonth *march __attribute__((swift_name("march")));
@property (class, readonly) ActualSyncKtor_utilsMonth *april __attribute__((swift_name("april")));
@property (class, readonly) ActualSyncKtor_utilsMonth *may __attribute__((swift_name("may")));
@property (class, readonly) ActualSyncKtor_utilsMonth *june __attribute__((swift_name("june")));
@property (class, readonly) ActualSyncKtor_utilsMonth *july __attribute__((swift_name("july")));
@property (class, readonly) ActualSyncKtor_utilsMonth *august __attribute__((swift_name("august")));
@property (class, readonly) ActualSyncKtor_utilsMonth *september __attribute__((swift_name("september")));
@property (class, readonly) ActualSyncKtor_utilsMonth *october __attribute__((swift_name("october")));
@property (class, readonly) ActualSyncKtor_utilsMonth *november __attribute__((swift_name("november")));
@property (class, readonly) ActualSyncKtor_utilsMonth *december __attribute__((swift_name("december")));
+ (ActualSyncKotlinArray<ActualSyncKtor_utilsMonth *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<ActualSyncKtor_utilsMonth *> *entries __attribute__((swift_name("entries")));
@property (readonly) NSString *value __attribute__((swift_name("value")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_utilsGMTDate.Companion")))
@interface ActualSyncKtor_utilsGMTDateCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_utilsGMTDateCompanion *shared __attribute__((swift_name("shared")));
- (id<ActualSyncKotlinx_serialization_coreKSerializer>)serializer __attribute__((swift_name("serializer()")));
@property (readonly) ActualSyncKtor_utilsGMTDate *START __attribute__((swift_name("START")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpHttpProtocolVersion.Companion")))
@interface ActualSyncKtor_httpHttpProtocolVersionCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_httpHttpProtocolVersionCompanion *shared __attribute__((swift_name("shared")));
- (ActualSyncKtor_httpHttpProtocolVersion *)fromValueName:(NSString *)name major:(int32_t)major minor:(int32_t)minor __attribute__((swift_name("fromValue(name:major:minor:)")));
- (ActualSyncKtor_httpHttpProtocolVersion *)parseValue:(id)value __attribute__((swift_name("parse(value:)")));
@property (readonly) ActualSyncKtor_httpHttpProtocolVersion *HTTP_1_0 __attribute__((swift_name("HTTP_1_0")));
@property (readonly) ActualSyncKtor_httpHttpProtocolVersion *HTTP_1_1 __attribute__((swift_name("HTTP_1_1")));
@property (readonly) ActualSyncKtor_httpHttpProtocolVersion *HTTP_2_0 __attribute__((swift_name("HTTP_2_0")));
@property (readonly) ActualSyncKtor_httpHttpProtocolVersion *QUIC __attribute__((swift_name("QUIC")));
@property (readonly) ActualSyncKtor_httpHttpProtocolVersion *SPDY_3 __attribute__((swift_name("SPDY_3")));
@end

__attribute__((swift_name("KotlinKType")))
@protocol ActualSyncKotlinKType
@required

/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
@property (readonly) NSArray<ActualSyncKotlinKTypeProjection *> *arguments __attribute__((swift_name("arguments")));

/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
@property (readonly) id<ActualSyncKotlinKClassifier> _Nullable classifier __attribute__((swift_name("classifier")));
@property (readonly) BOOL isMarkedNullable __attribute__((swift_name("isMarkedNullable")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="2.0")
*/
__attribute__((swift_name("KotlinAutoCloseable")))
@protocol ActualSyncKotlinAutoCloseable
@required
- (void)close __attribute__((swift_name("close()")));
@end

__attribute__((swift_name("Kotlinx_io_coreRawSource")))
@protocol ActualSyncKotlinx_io_coreRawSource <ActualSyncKotlinAutoCloseable>
@required
- (int64_t)readAtMostToSink:(ActualSyncKotlinx_io_coreBuffer *)sink byteCount:(int64_t)byteCount __attribute__((swift_name("readAtMostTo(sink:byteCount:)")));
@end

__attribute__((swift_name("Kotlinx_io_coreSource")))
@protocol ActualSyncKotlinx_io_coreSource <ActualSyncKotlinx_io_coreRawSource>
@required
- (BOOL)exhausted __attribute__((swift_name("exhausted()")));
- (id<ActualSyncKotlinx_io_coreSource>)peek __attribute__((swift_name("peek()")));
- (int32_t)readAtMostToSink:(ActualSyncKotlinByteArray *)sink startIndex:(int32_t)startIndex endIndex:(int32_t)endIndex __attribute__((swift_name("readAtMostTo(sink:startIndex:endIndex:)")));
- (int8_t)readByte __attribute__((swift_name("readByte()")));
- (int32_t)readInt __attribute__((swift_name("readInt()")));
- (int64_t)readLong __attribute__((swift_name("readLong()")));
- (int16_t)readShort __attribute__((swift_name("readShort()")));
- (void)readToSink:(id<ActualSyncKotlinx_io_coreRawSink>)sink byteCount:(int64_t)byteCount __attribute__((swift_name("readTo(sink:byteCount:)")));
- (BOOL)requestByteCount:(int64_t)byteCount __attribute__((swift_name("request(byteCount:)")));
- (void)requireByteCount:(int64_t)byteCount __attribute__((swift_name("require(byteCount:)")));
- (void)skipByteCount:(int64_t)byteCount __attribute__((swift_name("skip(byteCount:)")));
- (int64_t)transferToSink:(id<ActualSyncKotlinx_io_coreRawSink>)sink __attribute__((swift_name("transferTo(sink:)")));

/**
 * @note annotations
 *   kotlinx.io.InternalIoApi
*/
@property (readonly) ActualSyncKotlinx_io_coreBuffer *buffer __attribute__((swift_name("buffer")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpURLBuilder.Companion")))
@interface ActualSyncKtor_httpURLBuilderCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_httpURLBuilderCompanion *shared __attribute__((swift_name("shared")));
@end

__attribute__((swift_name("Ktor_httpParametersBuilder")))
@protocol ActualSyncKtor_httpParametersBuilder <ActualSyncKtor_utilsStringValuesBuilder>
@required
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpURLProtocol.Companion")))
@interface ActualSyncKtor_httpURLProtocolCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_httpURLProtocolCompanion *shared __attribute__((swift_name("shared")));
- (ActualSyncKtor_httpURLProtocol *)createOrDefaultName:(NSString *)name __attribute__((swift_name("createOrDefault(name:)")));
@property (readonly) ActualSyncKtor_httpURLProtocol *HTTP __attribute__((swift_name("HTTP")));
@property (readonly) ActualSyncKtor_httpURLProtocol *HTTPS __attribute__((swift_name("HTTPS")));
@property (readonly) ActualSyncKtor_httpURLProtocol *SOCKS __attribute__((swift_name("SOCKS")));
@property (readonly) ActualSyncKtor_httpURLProtocol *WS __attribute__((swift_name("WS")));
@property (readonly) ActualSyncKtor_httpURLProtocol *WSS __attribute__((swift_name("WSS")));
@property (readonly) NSDictionary<NSString *, ActualSyncKtor_httpURLProtocol *> *byName __attribute__((swift_name("byName")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpHeaderValueParam")))
@interface ActualSyncKtor_httpHeaderValueParam : ActualSyncBase
- (instancetype)initWithName:(NSString *)name value:(NSString *)value __attribute__((swift_name("init(name:value:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithName:(NSString *)name value:(NSString *)value escapeValue:(BOOL)escapeValue __attribute__((swift_name("init(name:value:escapeValue:)"))) __attribute__((objc_designated_initializer));
- (ActualSyncKtor_httpHeaderValueParam *)doCopyName:(NSString *)name value:(NSString *)value escapeValue:(BOOL)escapeValue __attribute__((swift_name("doCopy(name:value:escapeValue:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) BOOL escapeValue __attribute__((swift_name("escapeValue")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) NSString *value __attribute__((swift_name("value")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpHeaderValueWithParameters.Companion")))
@interface ActualSyncKtor_httpHeaderValueWithParametersCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_httpHeaderValueWithParametersCompanion *shared __attribute__((swift_name("shared")));
- (id _Nullable)parseValue:(NSString *)value init:(id _Nullable (^)(NSString *, NSArray<ActualSyncKtor_httpHeaderValueParam *> *))init __attribute__((swift_name("parse(value:init:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_httpContentType.Companion")))
@interface ActualSyncKtor_httpContentTypeCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_httpContentTypeCompanion *shared __attribute__((swift_name("shared")));
- (ActualSyncKtor_httpContentType *)parseValue:(NSString *)value __attribute__((swift_name("parse(value:)")));
@property (readonly) ActualSyncKtor_httpContentType *Any __attribute__((swift_name("Any")));
@end


/**
 * @note annotations
 *   kotlinx.coroutines.InternalCoroutinesApi
*/
__attribute__((swift_name("Kotlinx_coroutines_coreParentJob")))
@protocol ActualSyncKotlinx_coroutines_coreParentJob <ActualSyncKotlinx_coroutines_coreJob>
@required

/**
 * @note annotations
 *   kotlinx.coroutines.InternalCoroutinesApi
*/
- (ActualSyncKotlinCancellationException *)getChildJobCancellationCause __attribute__((swift_name("getChildJobCancellationCause()")));
@end


/**
 * @note annotations
 *   kotlinx.coroutines.InternalCoroutinesApi
*/
__attribute__((swift_name("Kotlinx_coroutines_coreSelectInstance")))
@protocol ActualSyncKotlinx_coroutines_coreSelectInstance
@required
- (void)disposeOnCompletionDisposableHandle:(id<ActualSyncKotlinx_coroutines_coreDisposableHandle>)disposableHandle __attribute__((swift_name("disposeOnCompletion(disposableHandle:)")));
- (void)selectInRegistrationPhaseInternalResult:(id _Nullable)internalResult __attribute__((swift_name("selectInRegistrationPhase(internalResult:)")));
- (BOOL)trySelectClauseObject:(id)clauseObject result:(id _Nullable)result __attribute__((swift_name("trySelect(clauseObject:result:)")));
@property (readonly) id<ActualSyncKotlinCoroutineContext> context __attribute__((swift_name("context")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinEnumCompanion")))
@interface ActualSyncKotlinEnumCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKotlinEnumCompanion *shared __attribute__((swift_name("shared")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_utilsWeekDay.Companion")))
@interface ActualSyncKtor_utilsWeekDayCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_utilsWeekDayCompanion *shared __attribute__((swift_name("shared")));
- (ActualSyncKtor_utilsWeekDay *)fromOrdinal:(int32_t)ordinal __attribute__((swift_name("from(ordinal:)")));
- (ActualSyncKtor_utilsWeekDay *)fromValue:(NSString *)value __attribute__((swift_name("from(value:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Ktor_utilsMonth.Companion")))
@interface ActualSyncKtor_utilsMonthCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKtor_utilsMonthCompanion *shared __attribute__((swift_name("shared")));
- (ActualSyncKtor_utilsMonth *)fromOrdinal:(int32_t)ordinal __attribute__((swift_name("from(ordinal:)")));
- (ActualSyncKtor_utilsMonth *)fromValue:(NSString *)value __attribute__((swift_name("from(value:)")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinKTypeProjection")))
@interface ActualSyncKotlinKTypeProjection : ActualSyncBase
- (instancetype)initWithVariance:(ActualSyncKotlinKVariance * _Nullable)variance type:(id<ActualSyncKotlinKType> _Nullable)type __attribute__((swift_name("init(variance:type:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) ActualSyncKotlinKTypeProjectionCompanion *companion __attribute__((swift_name("companion")));
- (ActualSyncKotlinKTypeProjection *)doCopyVariance:(ActualSyncKotlinKVariance * _Nullable)variance type:(id<ActualSyncKotlinKType> _Nullable)type __attribute__((swift_name("doCopy(variance:type:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) id<ActualSyncKotlinKType> _Nullable type __attribute__((swift_name("type")));
@property (readonly) ActualSyncKotlinKVariance * _Nullable variance __attribute__((swift_name("variance")));
@end

__attribute__((swift_name("Kotlinx_io_coreRawSink")))
@protocol ActualSyncKotlinx_io_coreRawSink <ActualSyncKotlinAutoCloseable>
@required
- (void)flush __attribute__((swift_name("flush()")));
- (void)writeSource:(ActualSyncKotlinx_io_coreBuffer *)source byteCount:(int64_t)byteCount __attribute__((swift_name("write(source:byteCount:)")));
@end

__attribute__((swift_name("Kotlinx_io_coreSink")))
@protocol ActualSyncKotlinx_io_coreSink <ActualSyncKotlinx_io_coreRawSink>
@required
- (void)emit __attribute__((swift_name("emit()")));

/**
 * @note annotations
 *   kotlinx.io.InternalIoApi
*/
- (void)hintEmit __attribute__((swift_name("hintEmit()")));
- (int64_t)transferFromSource:(id<ActualSyncKotlinx_io_coreRawSource>)source __attribute__((swift_name("transferFrom(source:)")));
- (void)writeSource:(id<ActualSyncKotlinx_io_coreRawSource>)source byteCount_:(int64_t)byteCount __attribute__((swift_name("write(source:byteCount_:)")));
- (void)writeSource:(ActualSyncKotlinByteArray *)source startIndex:(int32_t)startIndex endIndex:(int32_t)endIndex __attribute__((swift_name("write(source:startIndex:endIndex:)")));
- (void)writeByteByte:(int8_t)byte __attribute__((swift_name("writeByte(byte:)")));
- (void)writeIntInt:(int32_t)int_ __attribute__((swift_name("writeInt(int:)")));
- (void)writeLongLong:(int64_t)long_ __attribute__((swift_name("writeLong(long:)")));
- (void)writeShortShort:(int16_t)short_ __attribute__((swift_name("writeShort(short:)")));

/**
 * @note annotations
 *   kotlinx.io.InternalIoApi
*/
@property (readonly) ActualSyncKotlinx_io_coreBuffer *buffer __attribute__((swift_name("buffer")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Kotlinx_io_coreBuffer")))
@interface ActualSyncKotlinx_io_coreBuffer : ActualSyncBase <ActualSyncKotlinx_io_coreSource, ActualSyncKotlinx_io_coreSink>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)clear __attribute__((swift_name("clear()")));
- (void)close __attribute__((swift_name("close()")));
- (ActualSyncKotlinx_io_coreBuffer *)doCopy __attribute__((swift_name("doCopy()")));
- (void)doCopyToOut:(ActualSyncKotlinx_io_coreBuffer *)out startIndex:(int64_t)startIndex endIndex:(int64_t)endIndex __attribute__((swift_name("doCopyTo(out:startIndex:endIndex:)")));
- (void)emit __attribute__((swift_name("emit()")));
- (BOOL)exhausted __attribute__((swift_name("exhausted()")));
- (void)flush __attribute__((swift_name("flush()")));
- (int8_t)getPosition:(int64_t)position __attribute__((swift_name("get(position:)")));

/**
 * @note annotations
 *   kotlinx.io.InternalIoApi
*/
- (void)hintEmit __attribute__((swift_name("hintEmit()")));
- (id<ActualSyncKotlinx_io_coreSource>)peek __attribute__((swift_name("peek()")));
- (int64_t)readAtMostToSink:(ActualSyncKotlinx_io_coreBuffer *)sink byteCount:(int64_t)byteCount __attribute__((swift_name("readAtMostTo(sink:byteCount:)")));
- (int32_t)readAtMostToSink:(ActualSyncKotlinByteArray *)sink startIndex:(int32_t)startIndex endIndex:(int32_t)endIndex __attribute__((swift_name("readAtMostTo(sink:startIndex:endIndex:)")));
- (int8_t)readByte __attribute__((swift_name("readByte()")));
- (int32_t)readInt __attribute__((swift_name("readInt()")));
- (int64_t)readLong __attribute__((swift_name("readLong()")));
- (int16_t)readShort __attribute__((swift_name("readShort()")));
- (void)readToSink:(id<ActualSyncKotlinx_io_coreRawSink>)sink byteCount:(int64_t)byteCount __attribute__((swift_name("readTo(sink:byteCount:)")));
- (BOOL)requestByteCount:(int64_t)byteCount __attribute__((swift_name("request(byteCount:)")));
- (void)requireByteCount:(int64_t)byteCount __attribute__((swift_name("require(byteCount:)")));
- (void)skipByteCount:(int64_t)byteCount __attribute__((swift_name("skip(byteCount:)")));
- (NSString *)description __attribute__((swift_name("description()")));
- (int64_t)transferFromSource:(id<ActualSyncKotlinx_io_coreRawSource>)source __attribute__((swift_name("transferFrom(source:)")));
- (int64_t)transferToSink:(id<ActualSyncKotlinx_io_coreRawSink>)sink __attribute__((swift_name("transferTo(sink:)")));
- (void)writeSource:(ActualSyncKotlinx_io_coreBuffer *)source byteCount:(int64_t)byteCount __attribute__((swift_name("write(source:byteCount:)")));
- (void)writeSource:(id<ActualSyncKotlinx_io_coreRawSource>)source byteCount_:(int64_t)byteCount __attribute__((swift_name("write(source:byteCount_:)")));
- (void)writeSource:(ActualSyncKotlinByteArray *)source startIndex:(int32_t)startIndex endIndex:(int32_t)endIndex __attribute__((swift_name("write(source:startIndex:endIndex:)")));
- (void)writeByteByte:(int8_t)byte __attribute__((swift_name("writeByte(byte:)")));
- (void)writeIntInt:(int32_t)int_ __attribute__((swift_name("writeInt(int:)")));
- (void)writeLongLong:(int64_t)long_ __attribute__((swift_name("writeLong(long:)")));
- (void)writeShortShort:(int16_t)short_ __attribute__((swift_name("writeShort(short:)")));

/**
 * @note annotations
 *   kotlinx.io.InternalIoApi
*/
@property (readonly) ActualSyncKotlinx_io_coreBuffer *buffer __attribute__((swift_name("buffer")));
@property (readonly) int64_t size __attribute__((swift_name("size")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinKVariance")))
@interface ActualSyncKotlinKVariance : ActualSyncKotlinEnum<ActualSyncKotlinKVariance *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) ActualSyncKotlinKVariance *invariant __attribute__((swift_name("invariant")));
@property (class, readonly) ActualSyncKotlinKVariance *in __attribute__((swift_name("in")));
@property (class, readonly) ActualSyncKotlinKVariance *out __attribute__((swift_name("out")));
+ (ActualSyncKotlinArray<ActualSyncKotlinKVariance *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<ActualSyncKotlinKVariance *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinKTypeProjection.Companion")))
@interface ActualSyncKotlinKTypeProjectionCompanion : ActualSyncBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) ActualSyncKotlinKTypeProjectionCompanion *shared __attribute__((swift_name("shared")));

/**
 * @note annotations
 *   kotlin.jvm.JvmStatic
*/
- (ActualSyncKotlinKTypeProjection *)contravariantType:(id<ActualSyncKotlinKType>)type __attribute__((swift_name("contravariant(type:)")));

/**
 * @note annotations
 *   kotlin.jvm.JvmStatic
*/
- (ActualSyncKotlinKTypeProjection *)covariantType:(id<ActualSyncKotlinKType>)type __attribute__((swift_name("covariant(type:)")));

/**
 * @note annotations
 *   kotlin.jvm.JvmStatic
*/
- (ActualSyncKotlinKTypeProjection *)invariantType:(id<ActualSyncKotlinKType>)type __attribute__((swift_name("invariant(type:)")));
@property (readonly) ActualSyncKotlinKTypeProjection *STAR __attribute__((swift_name("STAR")));
@end

#pragma pop_macro("_Nullable_result")
#pragma clang diagnostic pop
NS_ASSUME_NONNULL_END
