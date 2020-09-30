import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.print.attribute.standard.NumberUp;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.Buffer;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import static javax.swing.text.html.CSS.getAttribute;

public class Lab5 {

    public static void main(String[] args) {

        //TODO: add file loading
        ArrayList<Person> list = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File("resources/collection.xml"));
            document.getDocumentElement().normalize();
            Element root = document.getDocumentElement();
            NodeList person_list = document.getElementsByTagName("person");
            for(int i = 0; i < person_list.getLength(); i++){
                Node node = person_list.item(i);
                Element element = (Element) node;
                Person person = new Person();

                // init ID
                person.id = Integer.parseInt(element.getAttribute("id"));

                // init name
                person.name = element.getElementsByTagName("name").item(0).getTextContent();

                // init coordinates
                person.coordinates = new Coordinates();
                person.coordinates.x = Float.parseFloat(((Element) element.getElementsByTagName("coordinates").item(0))
                        .getElementsByTagName("x").item(0).getTextContent());
                person.coordinates.x = Integer.parseInt(((Element) element.getElementsByTagName("coordinates").item(0))
                        .getElementsByTagName("y").item(0).getTextContent());

                // init creation_date
                person.creationDate = ZonedDateTime.parse(element.getElementsByTagName("creationDate").item(0).getTextContent(),
                        DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z"));

                // init height
                person.height = Integer.parseInt(element.getElementsByTagName("height").item(0).getTextContent());

                // init passportID
                try {
                    person.passportID = element.getElementsByTagName("passportID").item(0).getTextContent();
                } catch(NullPointerException e){
                    person.passportID = null;
                }

                // init eyeColor
                try {
                    person.eyeColor = Color.valueOf(element.getElementsByTagName("eyeColor").item(0).getTextContent());
                } catch(NullPointerException e){
                    person.eyeColor = null;
                }

                // init nationality
                try {
                    person.nationality = Country.valueOf(element.getElementsByTagName("nationality").item(0).getTextContent());
                } catch(NullPointerException e){
                    person.nationality = null;
                }

                // init location
                try {
                    person.location = new Location();
                    person.location.x = Integer.parseInt(((Element) element.getElementsByTagName("location").item(0))
                            .getElementsByTagName("x").item(0).getTextContent());
                    person.location.y = Integer.parseInt(((Element) element.getElementsByTagName("location").item(0))
                            .getElementsByTagName("y").item(0).getTextContent());
                    person.location.z = Double.parseDouble(((Element) element.getElementsByTagName("location").item(0))
                            .getElementsByTagName("z").item(0).getTextContent());
                    try {
                        person.location.name = element.getElementsByTagName("nationality").item(0).getTextContent();
                    } catch (NullPointerException e) {
                        person.location.name = null;
                    }
                } catch (NullPointerException e){
                    person.location = null;
                }

                list.add(person);

            }
        } catch (ParserConfigurationException e) {
            System.out.println("Error reading file, starting with blank collection");
        } catch (SAXException e) {
            System.out.println("Error reading file, starting with blank collection");
        } catch (IOException e) {
            System.out.println("Error reading file, starting with blank collection");
        }

//        try {
//            FileReader collection_file = new FileReader("resources/collection.xml");
//            BufferedReader bufferedReader = new BufferedReader(collection_file);
//
//
//        } catch (FileNotFoundException e) {
//            System.out.println("No collection file found, starting with blank collection");
//        } catch (IOException e) {
//            System.out.println("Error reading file, starting with blank collection");
//        }

        Scanner console_scanner = new Scanner(System.in);

        Command[] commands = {
                new HelpCommand(),
                new InfoCommand(),
                new ShowCommand(),
                new AddElementCommand(),
                new UpdateElementOnIDCommand(),
                new RemoveByIdCommand(),
                new ClearCollectionCommand(),
                new SaveCollectionCommand(),
                new ExitCommand(),
                new InsertElementAtIndexCommand(),
                new AddElementIfMaxCommand(),
                new ShuffleCommand(),
                new MaxByHeight(),
                new PrintFieldAscendingHeightCommand(),
                new PrintFieldDescendingHeightCommand()
        };

        //noinspection InfiniteLoopStatement
        while(true){
            System.out.print("> ");
            String command_name = console_scanner.nextLine()  + " oopses_cather";
            boolean correct_command = false;
            if(command_name.split("\\s+")[0].equals("execute_script")) {
                correct_command = true;
                if(command_name.split("\\s+").length > 1) {
                    String file_location = command_name.split("\\s+")[1];
                    try {
                        Scanner script_scanner = new Scanner(new File(file_location));
                        while (script_scanner.hasNext()) {
                            command_name = script_scanner.nextLine() + " oopses_cather";
                            for (Command command_iterator : commands) {
                                if (command_iterator.inline_name().equals(command_name.split("\\s+")[0])) {
                                    list = command_iterator.execute(list, script_scanner,
                                            new PrintStream(new FileOutputStream("NUL:")), command_name.split("\\s+")[1]);
                                    correct_command = true;
                                    break;
                                }
                            }
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("Unable to find file at this location");
                    }
                }
                else
                    System.out.println("Usage: execute_script <file_location>");
            }
            else
                for (Command command_iterator : commands) {
                    if (command_iterator.inline_name().equals(command_name.split("\\s+")[0])) {
                        list = command_iterator.execute(list, console_scanner, System.out, command_name.split("\\s+")[1]);
                        correct_command = true;
                        break;
                    }
                }
            if(!correct_command){
                System.out.println("invalid command");
            }
        }
    }
}
