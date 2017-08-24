package routines;

import java.util.*;

/*
 * user specification: the function's comment should contain keys as follows: 1. write about the function's comment.but
 * it must be before the "{talendTypes}" key.
 * 
 * 2. {talendTypes} 's value must be talend Type, it is required . its value should be one of: String, char | Character,
 * long | Long, int | Integer, boolean | Boolean, byte | Byte, Date, double | Double, float | Float, Object, short |
 * Short
 * 
 * 3. {Category} define a category for the Function. it is required. its value is user-defined .
 * 
 * 4. {param} 's format is: {param} <type>[(<default value or closed list values>)] <name>[ : <comment>]
 * 
 * <type> 's value should be one of: string, int, list, double, object, boolean, long, char, date. <name>'s value is the
 * Function's parameter name. the {param} is optional. so if you the Function without the parameters. the {param} don't
 * added. you can have many parameters for the Function.
 * 
 * 5. {example} gives a example for the Function. it is optional.
 */
public class OcbOcdEnvProperties {

	private static Properties prop = null;
	private static Properties secProp = null; //use a separate properties object to hold security properties value
	
	private static String PROPERTY_FILE_ENV_UNIX = "/talend/odf/conf/ocb_ocd_env.properties";
	private static String PROPERTY_FILE_ENV_WIN = "c:/datafabric/config/ocb_ocd_env.properties";

	private static String PROPERTY_FILE_SECURITY_UNIX = "/talend/odf/conf/ocb_ocd_security.properties";
	private static String PROPERTY_FILE_SECURITY_WIN = "c:/datafabric/config/ocb_ocd_security.properties";
	
	
	
	public static String getProperty(String key){
		if(prop == null){
			initialize();
		}
		if(prop == null){
			System.err.println("Property file is not initialized. Please make sure properties file exist");
			System.exit(0);
		}
		String value = prop.getProperty(key);
		if(value == null){
			value = "";
		}
		
		return value;
	}
	public static Map<String, String> getAllProperties(){
		if(prop == null || secProp == null){
			initialize();
		}
		if(prop == null){
			System.err.println("Property file is not initialized. Please make sure properties file exist");
			System.exit(0);
		}		
		
		Set<String> propNames = prop.stringPropertyNames();
		Map<String, String> properties = new HashMap<String, String>();
		for(String each : propNames){
			String value = prop.getProperty(each);
			if(value == null){
				value = "";
			}
			
			//security properties must not be part of getAll, that's a risk
			if(!secProp.stringPropertyNames().contains(each)){
				properties.put(each, value);
			}
		}
		
		return properties;
	}
	
	private static void initialize(){
		if(prop != null){
			return;
		}
		prop = new java.util.Properties();
		secProp = new java.util.Properties();
		
		String envPropertiesFile = null;
		String securityPropertiesFile = null;
		
		//Check if the location of properties file is supplied at RunTime. 
		String overriddenEnvPropertiesFile = System.getProperty("configEnv");
		String overriddenSecurityPropertiesFile = System.getProperty("configSec");
		
		String os = System.getProperty("os.name").toLowerCase();
		
		//If the location of properties file is supplied at runtime via jvm property, then use it. Or else use the default location. 
		if (overriddenEnvPropertiesFile != null){
			envPropertiesFile = overriddenEnvPropertiesFile;
		} else {
			if (os.indexOf("win") >= 0){
				envPropertiesFile = PROPERTY_FILE_ENV_WIN;
			} else {
				envPropertiesFile = PROPERTY_FILE_ENV_UNIX;
			}
		}
		
		if (overriddenSecurityPropertiesFile != null){
			securityPropertiesFile = overriddenSecurityPropertiesFile;
		} else {
			if (os.indexOf("win") >= 0){
				securityPropertiesFile = PROPERTY_FILE_SECURITY_WIN;
			} else {
				securityPropertiesFile = PROPERTY_FILE_SECURITY_UNIX;
			}
		}
		load(envPropertiesFile,prop);
		load(securityPropertiesFile,secProp);
		
		//for backward compatibility, load security properties value to standard properties
		for(String each : secProp.stringPropertyNames()){
			String value = secProp.getProperty(each);
			prop.setProperty(each, value);
		}
		
	}
	
	private static void load(String file, Properties prop) {
		java.io.InputStream input = null;

		try {

			input = new java.io.FileInputStream(file);
			// load a properties file
			prop.load(input);


		} catch (java.io.IOException ex) {
			ex.printStackTrace();
			prop = null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (java.io.IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
