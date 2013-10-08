package org.openflexo.ws.jira;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.openflexo.ws.jira.JIRAClient.Method;
import org.openflexo.ws.jira.action.SubmitIssue;
import org.openflexo.ws.jira.model.JIRAField;
import org.openflexo.ws.jira.model.JIRAIssue;
import org.openflexo.ws.jira.model.JIRAIssue.IssueType;
import org.openflexo.ws.jira.model.JIRAObject;
import org.openflexo.ws.jira.model.JIRAProject;
import org.openflexo.ws.jira.model.JIRAProjectList;
import org.openflexo.ws.jira.result.JIRAResult;

public class TestJSon extends TestCase {

	public void testJSONSerialization() {
		String s = "{\r\n" + "    \"fields\": {\r\n" + "       \"project\":\r\n" + "       {\r\n" + "          \"key\": \"TEST\"\r\n"
				+ "       },\r\n" + "       \"summary\": \"REST ye merry gentlemen.\",\r\n"
				+ "       \"description\": \"Creating of an issue using project keys and issue type names using the REST API\",\r\n"
				+ "       \"issuetype\": {\r\n" + "          \"name\": \"Bug\"\r\n" + "       }\r\n" + "   }\r\n" + "}";
		SubmitIssue submit = JIRAGson.getInstance().fromJson(s, SubmitIssue.class);
		assertNotNull(submit);
		assertNotNull(submit.getFields());
		assertNotNull(submit.getFields().getProject());
		assertNotNull(submit.getFields().getProject().getKey());
		assertNotNull(submit.getFields().getSummary());
		assertNotNull(submit.getFields().getDescription());
		assertNotNull(submit.getFields().getIssuetype());
		assertNotNull(submit.getFields().getIssuetype().getName());
	}

	public void testSubmitIssue() throws IOException, JIRAException {
		if (true) {
			return;
		}
		JIRAClient client = getTestClient();
		SubmitIssue submit = new SubmitIssue();
		JIRAIssue issue = new JIRAIssue();
		submit.setFields(issue);
		issue.setDescription("Some quite long description that can come from a long textarea. On the next"
				+ " line we will try to put some HTML:\n\n\n" + "<b>bold</b> <i>italic</i><br/>" + "On a new line<br/>Another new line");
		IssueType issueType = new IssueType();
		issueType.setId(String.valueOf(1));
		issue.setIssuetype(issueType);
		issue.setSummary("Some test from a Java web client");
		JIRAProject project = new JIRAProject();
		project.setKey("OPENFLEXO");
		issue.setProject(project);
		JIRAResult response = client.submit(submit, Method.POST);
		issue.setId(response.getId());
		issue.setKey(response.getKey());
		issue.setKey("OPENFLEXO-12");
		System.err.println("Sending one file");
		client.attachFilesToIssue(issue, new File("D:\\My Documents\\Mes images\\2011-09-08\\005.JPG"));
		System.err.println("Sending two files");
		client.attachFilesToIssue(issue, null, new File("D:\\My Documents\\AVR130 Eng user manual.pdf"), new File(
				"C:\\Users\\Public\\Pictures\\Sample Pictures\\Penguins.jpg"));
	}

	private JIRAClient getTestClient() throws MalformedURLException {
		return new JIRAClient("https://bugs.openflexo.com", "", "");
	}

	public void testSubmitFromString() throws IOException, JIRAException {
		if (true) {
			return;
		}

		JIRAClient client = getTestClient();
		SubmitIssue submit = JIRAGson
				.getInstance()
				.fromJson(
						"{\"fields\":{\"project\":{\"id\":\"10000\"},\"summary\":\"\\u0027te\\u0027te\",\"description\":\"e\\u0027reserg\",\"issuetype\":{\"id\":\"1\"}}}",
						SubmitIssue.class);
		JIRAResult result = client.submit(submit, Method.POST);
		System.err.println(result.getId() + " " + result.getKey());
	}

	public void testProjectLoading() {
		JIRAProjectList projects = JIRAGson.getInstance().fromJson(
				new InputStreamReader(getClass().getResourceAsStream("/sample_project_list.json")), JIRAProjectList.class);
		assertNotNull(projects);
		assertEquals(1, projects.getProjects().size());
		JIRAProject project = projects.getProjects().get(0);
		assertNotNull(project.getId());
		assertNotNull(project.getKey());
		assertNotNull(project.getIssuetypes());
		assertTrue(project.getIssuetypes().size() > 0);
		for (IssueType issueType : project.getIssuetypes()) {
			assertNotNull(issueType.getId());
			assertNotNull(issueType.getName());
			assertTrue("Issue type " + issueType.getName() + " fields cannot be empty", issueType.getFields().size() > 0);
			validateFields(issueType, issueType.getComponentField(), "component");
			validateFields(issueType, issueType.getVersionField(), "version");
			validateFields(issueType, issueType.getPriorityField(), "priority");
		}
	}

	private void validateFields(IssueType issueType, JIRAField<?> field, String fieldName) {
		assertNotNull("Issue type " + issueType.getName() + " " + fieldName + " field must be present", field);
		assertTrue("Issue type " + issueType.getName() + " " + fieldName + " field must have at least one allowed value", field
				.getAllowedValues().size() > 0);
		for (JIRAObject<?> o : field.getAllowedValues()) {
			assertNotNull("Issue type " + issueType.getName() + " " + fieldName + " field must have allowed value with an id", o.getId());
		}
	}
}
