package vdsl.interpreter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vdsl.interpreter.core.Core;

public class Process {
	public String name = null;
	public List<Step> stepList = new ArrayList<Step>();
//	public List<TestStep> testStepList = new ArrayList<TestStep>();

	public Process(JSONObject obj) throws JSONException {
		name = obj.getString("name");
		JSONArray stepArray = obj.getJSONArray("steps");
		for (int i = 0; i < stepArray.length(); i++) {
			Step step = new Step(new JSONObject(stepArray.getString(i)));
			stepList.add(step);
		}

		if(Variable.getBoolean("Test_Enabled", false)){
			JSONArray testStepArray = obj.getJSONArray("test_step");
			if (testStepArray != null) {
				for (int i = 0; i < testStepArray.length(); i++) {
//					TestStep testStep = new TestStep(new JSONObject(testStepArray.getString(i)));
//					testStepList.add(testStep);
					Step testStep = new Step(new JSONObject(testStepArray.getString(i)), true);
					stepList.add(testStep);
				}
			}
		}

	}

	public void execute() {
		Iterator<Step> stepIt = stepList.iterator();
		boolean skipIterating = false;
		String userInput;
		Step step = null;
		while (stepIt.hasNext()) {
			if (skipIterating) {
				skipIterating = false;
			} else {
				step = (Step) stepIt.next();
			}
			System.out.println(step.name);

			if (Variable.getString("automatedRun") != null
					&& Variable.getString("automatedRun").trim().equalsIgnoreCase("true")) {
					step.getInput();
					step.doProcessing();
					step.setOutput();
					try {
						if (Core.isInteger(Variable.getString("delayBetweenSteps")))
							Thread.sleep(Integer.parseInt(Variable.getString("delayBetweenSteps")));
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
			} else {

				@SuppressWarnings("resource")
				Scanner in = new Scanner(System.in);
				userInput = in.nextLine();
				if (userInput.equalsIgnoreCase("1") || userInput.equalsIgnoreCase("i")
						|| userInput.equalsIgnoreCase("no")) {
					step.getInput();
					step.doProcessing();
				} else if (userInput.equalsIgnoreCase("0")) {

				} else if (userInput.equalsIgnoreCase("re") || userInput.equalsIgnoreCase("r")) {
					skipIterating = true;
				} else if (userInput.trim().equalsIgnoreCase("end")) {
					break;
				} else {
					step.getInput();
					step.doProcessing();
					step.setOutput();
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Process [Name=" + name + ", stepList=" + stepList + "]";
	}

}
