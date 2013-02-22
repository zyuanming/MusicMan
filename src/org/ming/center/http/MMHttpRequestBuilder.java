package org.ming.center.http;

import java.text.SimpleDateFormat;

import org.ming.center.ConfigSettingParameter;
import org.ming.center.GlobalSettingParameter;
import org.ming.center.business.MusicBusinessDefine_Net;
import org.ming.center.business.MusicBusinessDefine_WAP;
import org.ming.util.MyLogger;
import org.ming.util.Util;

public class MMHttpRequestBuilder
{
	private static int change = 0;
	private static final MyLogger logger = MyLogger
			.getLogger("MMHttpRequestBuilder");

	private static MMHttpRequest buildHttpRequest(MMHttpRequest mmhttprequest,
			int i)
	{
		String s = null;
		switch (i)
		{
		case 1000:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("serviceinfo.do").toString();
		}
			break;
		case 1001:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("login.do").toString();
		}
			break;
		case 1002:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("sub/musicinfo.do").toString();
		}
			break;
		case 1003:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("group.do").toString();
		}
			break;
		case 1004:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("group.do").toString();
		}
			break;
		case 1005:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("sub/playnext.do").toString();
		}
			break;
		case 1006:
		case 1010:
		case 1012:
		case 1013:
		case 1015:
		case 1019:
		case 1020:
		case 1021:
		case 1022:
		case 1023:
		case 1024:
		case 1025:
		case 1026:
		case 1027:
		case 1029:
		case 1030:
		case 1031:
		case 1036:
		case 1059:
		default:
		{
			mmhttprequest.setURL(s);
			return mmhttprequest;
		}
		case 1007:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("newsinfo.do").toString();
		}
			break;
		case 1008:
		case 1009:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("update-check.do").toString();
		}
			break;
		case 1011:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("searchcontent.do").toString();
		}
			break;
		case 1014:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("downloadlist.do").toString();
			mmhttprequest.setPostMethod(true);
		}
			break;
		case 1016:
		{
			s = "";
		}
			break;
		case 1017:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("albuminfo.do").toString();
		}
			break;
		case 1018:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("singerinfo.do").toString();
		}
			break;
		case 1028:
		{
			s = "net_play_list.xml";
		}
			break;
		case 1032:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("/MicroBlog/getsharelink.do").toString();
		}
			break;
		case 1033:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("tone/toneinfo.do").toString();
		}
			break;
		case 1034:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("tone/toneset.do").toString();
		}
			break;
		case 1035:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("ring/downloadinfo.do").toString();
		}
			break;
		case 1037:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("sub/subscription.do").toString();
		}
			break;
		case 1038:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("sub/cancelsub.do").toString();
		}
			break;
		case 1039:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("drm/cancelsub.do").toString();
		}
			break;
		case 1040:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("drm/subscription.do").toString();
		}
			break;
		case 1041:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("info.do").toString();
		}
			break;
		case 1042:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("member/usermember.do").toString();
			mmhttprequest.setPostMethod(true);
		}
			break;
		case 1043:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("tone/baseset.do").toString();
		}
			break;
		case 1044:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("tone/tonedel.do?").toString();
		}
			break;
		case 1045:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("tone/tonelist.do").toString();
		}
			break;
		case 1046:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("playlist.do").toString();
			mmhttprequest.setPostMethod(true);
		}
			break;
		case 1047:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("drm/songinfo.do").toString();
		}
			break;
		case 1048:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("tone/subscription.do").toString();
		}
			break;
		case 1049:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("songmark.do").toString();
		}
			break;
		case 1050:
		case 1051:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("personal_list.do").toString();
		}
			break;
		case 1052:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("drm/present.do").toString();
			mmhttprequest.setPostMethod(true);
		}
			break;
		case 1053:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("drm/recommend.do").toString();
			mmhttprequest.setPostMethod(true);
		}
			break;
		case 1054:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("comment.do").toString();
			mmhttprequest.setPostMethod(true);
		}
			break;
		case 1055:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("drm/operaterecord.do").toString();
			mmhttprequest.setPostMethod(true);
			mmhttprequest.setContentType("binary/octet-stream");
			mmhttprequest.setContentEncoding("utf-8");
		}
			break;
		case 1056:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("ring/operaterecord.do").toString();
		}
			break;
		case 1057:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("sub/querymonth.do").toString();
		}
			break;
		case 1058:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("radio.do").toString();
		}
			break;
		case 1060:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("tone/cancelsub.do").toString();
		}
			break;
		case 1061:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_WAP.CMWAP_HOST_NAME)))
					.append("reportError.do").toString();
		}
			break;
		}
		mmhttprequest.setURL(s);
		return mmhttprequest;
	}

	private static MMHttpRequest buildHttpsRequest(MMHttpRequest mmhttprequest,
			int i)
	{
		String s;
		if (i != 5000 && i != 5001 && i != 5002 && i != 5003 && i != 5067
				&& i != 5066)
		{
			mmhttprequest.addHeader("x-up-calling-line-id",
					GlobalSettingParameter.loginMobileNum);
			mmhttprequest.addHeader("randomsessionkey",
					GlobalSettingParameter.loginRadomNum);
		}
		s = null;
		switch (i)
		{
		case 5009:
		case 5014:
		case 5016:
		case 5017:
		case 5019:
		case 5023:
		case 5024:
		case 5025:
		case 5026:
		case 5027:
		case 5028:
		case 5029:
		case 5030:
		case 5031:
		case 5033:
		case 5034:
		case 5035:
		case 5039:
		case 5058:
		case 5063:
		default:
		{
			mmhttprequest.setURL(s);
			return mmhttprequest;
		}
		case 5000:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("verificationcode.do").toString();
		}
			break;
		case 5001:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("register.do").toString();
		}
			break;
		case 5002:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("resetpassword.do").toString();
		}
			break;
		case 5003:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("login.do").toString();
		}
			break;
		case 5004:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("serviceinfo.do").toString();
		}
			break;
		case 5005:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("sub/musicinfo.do").toString();
		}
			break;
		case 5006:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("group.do").toString();
		}
			break;
		case 5007:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("group.do").toString();
		}
			break;
		case 5008:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("sub/playnext.do").toString();
		}
			break;
		case 5010:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("newsinfo.do").toString();
		}
			break;
		case 5011:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("update-check.do").toString();
		}
			break;
		case 5012:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("update-check.do").toString();
		}
			break;
		case 5013:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("/MicroBlog/getsharelink.do").toString();
		}
			break;
		case 5015:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("searchcontent.do").toString();
		}
			break;
		case 5018:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("downloadlist.do").toString();
			mmhttprequest.setPostMethod(true);
		}
			break;
		case 5020:
		{
			s = "";
		}
			break;
		case 5021:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("albuminfo.do").toString();
		}
			break;
		case 5022:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("singerinfo.do").toString();
		}
			break;
		case 5032:
		{
			s = "net_play_list.xml";
		}
			break;
		case 5036:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("tone/toneinfo.do").toString();
		}
			break;
		case 5037:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("tone/toneset.do").toString();
		}
			break;
		case 5038:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("ring/downloadinfo.do").toString();
		}
			break;
		case 5040:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("sub/subscription.do").toString();
		}
			break;
		case 5041:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("sub/cancelsub.do").toString();
		}
			break;
		case 5042:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("drm/cancelsub.do").toString();
		}
			break;
		case 5043:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("drm/subscription.do").toString();
		}
			break;
		case 5044:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("info.do").toString();
		}
			break;
		case 5045:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("member/usermember.do").toString();
		}
			break;
		case 5046:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("tone/baseset.do").toString();
		}
			break;
		case 5047:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("tone/tonedel.do").toString();
		}
			break;
		case 5048:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("tone/tonelist.do").toString();
		}
			break;
		case 5049:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("playlist.do").toString();
			mmhttprequest.setPostMethod(true);
		}
			break;
		case 5050:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("drm/songinfo.do").toString();
		}
			break;
		case 5051:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("tone/subscription.do").toString();
		}
			break;
		case 5052:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("songmark.do").toString();
		}
			break;
		case 5053:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("personal_list.do").toString();
		}
			break;
		case 5054:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("personal_list.do").toString();
		}
			break;
		case 5055:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("drm/recommend.do").toString();
			mmhttprequest.setPostMethod(true);
		}
			break;
		case 5056:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("drm/present.do").toString();
			mmhttprequest.setPostMethod(true);
		}
			break;
		case 5057:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("comment.do").toString();
			mmhttprequest.setPostMethod(true);
		}
			break;
		case 5059:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("drm/operaterecord.do").toString();
			mmhttprequest.setURL(s);
			mmhttprequest.setPostMethod(true);
			mmhttprequest.setContentType("binary/octet-stream");
			mmhttprequest.setContentEncoding("utf-8");
		}
			break;
		case 5060:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("ring/operaterecord.do").toString();
		}
			break;
		case 5061:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("sub/querymonth.do").toString();
		}
			break;
		case 5062:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("radio.do").toString();
		}
			break;
		case 5064:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("tone/cancelsub.do").toString();
		}
			break;
		case 5065:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("reportError.do").toString();
		}
			break;
		case 5066:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("getcode.do").toString();
		}
			break;
		case 5067:
		{
			s = (new StringBuilder(
					String.valueOf(MusicBusinessDefine_Net.NET_HOST_NAME)))
					.append("quicklogin.do").toString();
		}
			break;
		}
		mmhttprequest.setURL(s);
		return mmhttprequest;
	}

	public static MMHttpRequest buildRequest(int i)
	{
		logger.v("buildRequest() ---> Enter");
		logger.v("i ----> " + i);
		MMHttpRequest mmhttprequest = new MMHttpRequest();
		mmhttprequest.setReqType(i);
		mmhttprequest.addUrlParams("version",
				GlobalSettingParameter.LOCAL_PARAM_VERSION);
		mmhttprequest.addUrlParams("ua",
				GlobalSettingParameter.LOCAL_PARAM_USER_AGENT);
		if (mmhttprequest.getValueOfUrlParams("contentid") != null
				&& !mmhttprequest.getValueOfUrlParams("contentid").equals(""))
		{
			String s = GlobalSettingParameter.SERVER_INIT_PARAM_MDN;
			long l = System.currentTimeMillis();
			mmhttprequest.addHeader("randkey", Util.getRandKey(s,
					(new SimpleDateFormat("yyyyMMddhhmmss")).format(Long
							.valueOf(l)), mmhttprequest
							.getValueOfUrlParams("contentid")));
		}
		mmhttprequest.addHeader("channel",
				ConfigSettingParameter.CONSTANT_CHANNEL_VALUE);
		mmhttprequest.addHeader("subchannel",
				ConfigSettingParameter.CONSTANT_SUBCHANNEL_VALUE);
		mmhttprequest.addHeader("randomsessionkey",
				GlobalSettingParameter.SERVER_INIT_RANDOMSESSIONKEY);
		mmhttprequest.addHeader("imei",
				GlobalSettingParameter.UPDATE_TAG_IMEI_INFO);
		mmhttprequest.addHeader("imsi",
				GlobalSettingParameter.UPDATE_TAG_IMSI_INFO);
		MMHttpRequest mmhttprequest1;
		if (i < 5000)
		{
			mmhttprequest
					.setProxyHost(MusicBusinessDefine_WAP.CMCC_WAP_PROXY_HOST);
			mmhttprequest
					.setProxyPort(MusicBusinessDefine_WAP.CMCC_WAP_PROXY_PORT);
			mmhttprequest1 = buildHttpRequest(mmhttprequest, i);
		} else
		{
			mmhttprequest1 = buildHttpsRequest(mmhttprequest, i);
		}
		logger.v("buildRequest() ---> Exit");
		return mmhttprequest1;
	}
}