{ coucou
dfs
dsf
fsdfsd
dssdffsdsfd
	DenaliCore = {
		mainProjectID= "TST";/* Internal use */
		defaultLanguage = "EN"; /* Iso code */
		configurationName = "DEV";
		applicationStyle = "Contento";
		permanentPageCacheSize = "5";
		pageCacheSize = 5; /* Number of pages the server holds for a user */
		sessionTimeOut = 14400; /* time of inactivity in seconds before a session expires (does not overwrite Tomcat session timeout!) */
		tmpDirPath="Temp/TestCG17913/"; /* Temporary folder where generated files are stored temporarily */
	};
	
	DenaliComponents = {
		defaultDateFormat = "%d-%b-%y"; /* This means that dates should look like this: 12-Oct-2005 */
	};
		
	DenaliCountry = {
		loadCountryAndLanguagesAtStart = false;
	};
	
	DenaliDBUtils = {
    		globalDefaultConnectionDictionary = {
        		username = "";
        		password = "";
        		URL = "jdbc:Oracle:thin:@(description=(address_list=(address=(protocol=tcp)(host=host)(port=1521)))(connect_data=(SERVER=DEDICATED)(SERVICE_NAME=SRV_NAME)))";
    		};
    };
	
	
	DenaliLocalization = {
		localizationFiles = {
			frameworks = (
				{
					name="DenaliCore";
					files=(WDLLocalizationEN);
				},{
					name="DenaliCore";
					files=(DLLocalizationEN);
				}
	        );
    		};
	};
	
}
