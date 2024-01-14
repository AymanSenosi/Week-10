package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectApp {

	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;

	// @formatter:off
	// This instance variable holds the list of operations for my menu.
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project"
			);
	// @formatter:on

	public static void main(String[] args) {
		// This object calls the method that processes the menu.
		new ProjectApp().processUserSelections();

	}

	/*
	 * This method displays the menu selections, gets a selection from the user,
	 * then acts on the selection.
	 */
	private void processUserSelections() {
		boolean done = false;
		// This will loop until the variable done is true.
		while (!done) {
			try {
				int selection = getUserSelection();
				switch (selection) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
					break;
				}

			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again.");
				e.printStackTrace();
			}
		}

	}

	private void selectProject() {
		listProjects();

		Integer projectId = getIntInput("Enter a project ID to select a project");
		curProject = null;

		/*
		 * This code calls the method fetchProjectById on the projectService object, and
		 * takes projectId as a parameter. Then returns a Project object to the
		 * curProject instance variable.
		 * 
		 * This will throw an exception if an invalid project ID is entered
		 */
		curProject = projectService.fetchProjectById(projectId);

		 // checks to see if curProject is null. If so, prints a message to the console.
		
		if (Objects.isNull(curProject)) {
			System.out.println("Invalid project ID selected");
		}

	}

	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();

		System.out.println("\nProjects:");

		projects.forEach(project -> System.out.println("  " + project.getProjectId() + ": " + project.getProjectName()));
	}

	/*
	 * This method accepts input from the user, then calls the project service to
	 * create a row.
	 */
	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");

		Project project = new Project();

		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	}

	// This method accepts input from the user as a BigDecimal.
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}

	private boolean exitMenu() {
		System.out.println("Exiting the menu.");
		return true;
	}

	/*
	 * This method will print the operations then accepts the user input as an
	 * Integer.
	 */
	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput("Enter a menu selection");

		return Objects.isNull(input) ? -1 : input;
	}

	// This method accepts input from the user and converts it into an Integer.
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}

	// This method prints the prompts, and gets the input from the user.
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();

		return input.isBlank() ? null : input.trim();
	}

	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");
		operations.forEach(line -> System.out.println(" " + line));

		if (Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		} else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}

}
