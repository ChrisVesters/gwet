package framework;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * 
 * @author cvesters
 */
public abstract class GwetTest
{
	/**
	 * 
	 * This should be placed in a README
	 * 
	 * The sets has a strict order.
	 * 1. all givens should be specified,
	 * 2. one optional actor (when),
	 * 3. one execution (executes),
	 * 4. verification of action (then). Each executes can be followed by one or more then's
	 * 
	 * Each test should verify one thing, and one thing only.
	 * 
	 * 
	 **/
	
	private final ArrayList<ISetting> fGivens = new ArrayList<ISetting>();
	private IActor fActor = null;
	private IAction fAction = null;
	private IResult fResult = null;
	
	public final String className = getClass().getSimpleName();
	@Rule
	public final TestName fTestName = new TestName();
	private long fStart;
	
	
	/**
	 * 
	 * 
	 * 
	 **/
	@Before
	public void setupTest()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("Started test: ");
		builder.append(className).append(".").append(fTestName.getMethodName());
		builder.append(".");
		log(builder.toString());
		fStart = System.currentTimeMillis();
	}
	
	
	@After
	public void teardownTest()
	{
		log(fAction);
		System.out.println("[UNDO] " + fAction.getDescription() + "\n");
		
		try
		{
			fAction.undo();
		}
		catch (final Throwable t)
		{
			log(t);
			fail("Tearing down: '" + fAction.toString() + "' failed. " + t.getMessage());
		}
		
		for (final ISetting setting : fGivens)
		{
			System.out.println("[TEARDOWN] " + setting.getDescription());

			try
			{
				setting.teardown();
			}
			catch (Throwable t)
			{
				log(t);
				fail("Tearing down: '" + setting.getDescription() + "' failed. " + t.getMessage()); // Can we fail here?
			}
		}
		
		final long elapsed = System.currentTimeMillis() - fStart;
		
		final StringBuilder builder = new StringBuilder();
		builder.append("Ended test: ");
		builder.append(className).append(".").append(fTestName.getMethodName());
		builder.append(" in ").append(elapsed).append("ms.");
		log(builder.toString());
	}
	
	
	protected void given(final ISetting setting)
	{
		assertNotNull("Invalid GWET Test: given(null).", setting);
		assertNull("Invalid GWET Test sequence. Encountered a 'given' after a 'when'.", fActor);
		assertNull("Invalid GWET Test sequence. Encountered a 'given' after an 'executes'.", fAction);
		
		log(setting);
		
		try
		{
			setting.setup();
			fGivens.add(setting);
		}
		catch (final Throwable t)
		{
			log(t);
			fail("Setting up: '" + setting.getDescription() + "' failed. " + t.getMessage());
		}
	}
	
	protected final void when(final IActor actor)
	{
		assertTrue("Invalid GWET Test: given(null).", actor != null);
		assertTrue("Invalid GWET Test sequence. Encountered a 'when' after another 'when'.", fActor == null);
		assertTrue("Invalid GWET Test sequence. Encountered a 'when' after an 'executes'.", fAction == null);
		
		log(actor);
		
		System.out.println("[WHEN] " + actor.getDescription());
		fActor = actor;
	}
	
	
	protected final void executes(final IAction action)
	{
		assertTrue("Invalid GWET Test: executes(null).", action != null);
		assertTrue("Invalid GWET Test sequence. Encountered an 'exeuctes' after another 'executes'.", fAction == null);
		
		log(action);
		System.out.println("[EXECUTES] " + action.getDescription());
		
		try
		{
			fResult = null;
			fResult = fActor == null ? action.execute() : fActor.execute(action);
			fAction = action;
		}
		catch (final Throwable t)
		{
			System.out.println(t);
			fail("Executing: '" + action.getDescription() + "' failed. " + t.getMessage());
		}
		
		assertTrue("Invalid GWET Test: executes() returns 'null'.", fResult != null);
	}
	
	protected final void then(final IResult result)
	{
		assertTrue("Invalid GWET Test: then(null).", result != null);
		assertTrue("Invalid GWET Test sequence. Encountered a 'then' before 'executes'.", fAction != null);
		assertTrue("Invalid GWET Test sequence. Encountered a 'then' before 'executes'.", fResult != null);
		
		log(result);
		// The expected result should be a super class of the actual result.
		// If not, inevitably there would be unresolved fields.
		final Diff diff;
		if (result.getClass().isAssignableFrom(fResult.getClass()))
		{
			diff = result.compareTo(fResult);
		}
		else
		{
			diff = new Diff();
			diff.add("Result Type", this.getClass(), result.getClass());
		}
		
		log(diff);
		
		final StringBuilder builder = new StringBuilder();
		log(builder, diff.getDiffInformation());
		assertTrue(builder.toString(), diff.isEmpty());
	}
	
	
	
	
	private void log(final ISetting setting)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("[GIVEN] ").append(setting.toString());
		
		final Map<String, String[]> info = setting.getSettingInformation();
		assertNotNull("Invalid GWET test: getSettingInformation() returns 'null'.", info);
		
		if (info.size() > 0)
		{
			builder.append("\n");
			log(builder, info);
		}
		
		log(builder.toString());
	}
	
	private void log(final IActor actor)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("[WHEN] ").append(actor.toString());
		
		final Map<String, String[]> info = actor.getActorInformation();
		assertNotNull("Invalid GWET test: getActorInformation() returns 'null'.", info);
		
		if (info.size() > 0)
		{
			builder.append("\n");
			log(builder, info);
		}
		
		log(builder.toString());
	}
	
	private void log(final IAction action)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("[WHEN] ").append(action.toString());
		
		final Map<String, String[]> info = action.getActionInformation();
		assertNotNull("Invalid GWET test: getActionInformation() returns 'null'.", info);
		
		if (info.size() > 0)
		{
			builder.append("\n");
			log(builder, info);
		}
		
		log(builder.toString());
	}
	
	private void log(final IResult result)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("[THEN] ").append(result.toString());
		
		final Map<String, String[]> info = result.getResultInformation();
		assertNotNull("Invalid GWET test: getActorInformation() returns 'null'.", info);
		
		if (info.size() > 0)
		{
			builder.append("\n");
			log(builder, info);
		}
		
		log(builder.toString());
	}
	
	
	private void log(final Diff diff)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("[DIFF] ").append(diff.toString());
		
		final Map<String, String[]> info = diff.getDiffInformation(true);
		assertNotNull("Invalid GWET test: getDiffInformation() returns 'null'.", info);
		
		if (info.size() > 0)
		{
			builder.append("\n");
			log(builder, info);
		}
		
		log(builder.toString());
	}
	
	
	/**
	 * 
	 * Append the information of the info map to the builder.
	 * This method enforces uniform logging.
	 * 
	 * @param builder The builder to which we want to append the information.
	 * @param info A map containing all information in the form of a key - values.
	 *
	 **/
	private void log(final StringBuilder builder, final Map<String, String[]> info)
	{
		assert info != null: "The information map can not be null.";
		
		for (final String key : info.keySet())
		{
			assertNotNull("Invalid GWET test: The information map contains a key without values.", info.get(key));
			assertTrue("Invalid GWET test: The information map contains a key without values.", info.get(key).length > 0);
			
			builder.append(key).append(" = ").append(" {");
			for (final String value : info.get(key))
			{
				builder.append("'").append(value).append("', ");
			}
			
			builder.delete(builder.length() - 2, builder.length() + 1);
			builder.append("}");
		}
	}
	
	/**
	 * 
	 * Log the stacktrace of any throwable.
	 * 
	 * @param t The throwable of which we want to log the stacktrace.
	 * 
	 **/
	private void log (final Throwable t)
	{
		final StringBuilder builder = new StringBuilder();
		for (final StackTraceElement trace : t.getStackTrace())
		{
			builder.append(trace).append("\n");
		}
		log(builder.toString());
	}
	
	
	/**
	 * 
	 * Write a string to the log.
	 * 
	 * @param string The message we want to write to the log.
	 * 
	 **/
	private void log(final String string)
	{
		System.out.println(string);
	}
}
