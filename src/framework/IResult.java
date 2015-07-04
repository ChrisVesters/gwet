package framework;

import java.util.Map;

/**
 * 
 * @author cvesters
 */
public interface IResult
{
	/**
	 * Compare this result to another, generating any differences found.
	 * Note: This method does not have to be symmetrical.
	 * The result will verify that all it's requirements can be found in the specified result.
	 * 
	 * @param result The result to which we want to compare.
	 * @return All differences between this and the specified result.
	 **/
	public Diff compareTo(final IResult result);
	
	public String getDescription();
	
	public Map<String, String[]> getResultInformation();
}
