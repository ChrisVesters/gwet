package framework;

import java.util.Map;

/**
 * 
 * @author cvesters
 */
public interface IActor
{
	public IResult execute(final IAction action) throws Throwable;
	
	public String getDescription();
	
	public Map<String, String[]> getActorInformation();
}
