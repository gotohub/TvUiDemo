package io.viva.tv.net.network;

import java.util.HashMap;
import java.util.Map;

public class LableMap {
	private static Map<String, String> lableMap = new HashMap<String, String>();
	private static String[] lableList = { "date", "time", "state", "type", "netID", "speed", "roaming", "bgdata", "interface", "ip", "data_activity", "cell_type", "cell_location",
			"phone_type" };

	public LableMap() {
		lableMap.put("date", "key_date_name");
		lableMap.put("time", "key_time_name");
		lableMap.put("state", "key_state_name");
		lableMap.put("interface", "key_interface_name");
		lableMap.put("type", "key_type_name");
		lableMap.put("netID", "key_netid_name");
		lableMap.put("roaming", "key_roaming_name");
		lableMap.put("bgdata", "key_bgdata_name");
		lableMap.put("ip", "key_ip_name");
		lableMap.put("gateway", "key_gateway_name");
		lableMap.put("speed", "key_speed_name");
		lableMap.put("dns", "key_dns_name");
		lableMap.put("data_activity", "key_data_activity_name");
		lableMap.put("cell_type", "key_cell_type_name");
		lableMap.put("cell_location", "key_cell_location_name");
		lableMap.put("phone_type", "key_phone_type_name");
	}

	public String getLable(String paramString) {
		return (String) lableMap.get(paramString);
	}

	public String[] getLableList() {
		return lableList;
	}
}