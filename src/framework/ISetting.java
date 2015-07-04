package framework;

import java.util.Map;

/**
 * 
 * @author cvesters
 */
public interface ISetting
{
	// Exceptions should not be handled internally but be thrown.
	public void setup() throws Throwable;
	
	public void teardown() throws Throwable;
	
	public String getDescription();
	
	public Map<String, String[]> getSettingInformation();
}
