package framework;

import java.util.Map;

/**
 * 
 * @author cvesters
 */
public interface IAction
{
	public IResult execute() throws Throwable;
	
	public void undo() throws Throwable;
	
	public String getDescription();
	
	public Map<String, String[]> getActionInformation();
}
