package lz.dubbo.trace;

public class TraceClientConst {


    public static final char   SEPARATOR            = '#';

    public static final int    MAX_SAMPLE_TRACE_NUM = 10;
    public static final int    TOP_MAX_SZIE         = 10;
    public static final String SERVER_IP_ALL        = "ALL";
    public static final int    TRACEID_LENGTH       = 7;

    public static final String EXCEPTION_KEY        = "exception";
    public static final String EXCEPTION_NAME_KEY   = "ex.name";
    public static final String EXCEPTION_TYPE       = "ex";
    public static final String INFO_TYPE            = "info";
    public static final String CONCURRENT_KEY       = "concurrent";
    public static final String NULL_STRING          = "NULL";

    public static final String START_PAGE_NUM          = "0";


	public static final String INIT_RPC_ID = null;

	public static final String SPAN_ID = null;

	public static final String PARENT_ID = null;

	public static final String RPC_ID = null;

	public static final String TRACE_ID = "traceId";
}
