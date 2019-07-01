import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromTerm;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
//enable time frame reads
public class Gmail_Access
{
    String saveDirectory = "Desktop";
    public static void main (String [] args) throws IOException, MessagingException {
        Gmail_Access gmail = new Gmail_Access();
        gmail.read();

    }

    public void read() throws MessagingException, IOException {

        Properties props = new Properties();

        try {
            // Setting protocol properties, and accessing inbox
            props.load(new FileInputStream(new File("src/smtp.properties")));
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("smtp.gmail.com", "*************@gmail.com", "your_password");
            Folder inbox = store.getFolder("inbox");
            inbox.open(Folder.READ_ONLY);
            // Checking for unread emails
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
            // Checking for unread emails from a particular sender
            FromTerm senderFromTerm = new FromTerm(new InternetAddress("specifieduser@gmail.com"));
            Message messages[] = inbox.search(unseenFlagTerm);
            messages = inbox.search(senderFromTerm, messages);
            // if no new message
            if (messages.length == 0)
                System.out.println("No messages found.");
            //found message, now check for attachments
            else
                for(int i = 0; i < messages.length; i++)
                {
                    Message email = messages[i];
                    String contentType = email.getContentType();
                    //contains attachments
                    if (contentType.contains("multipart"))
                    {
                        Multipart multipart = (Multipart) email.getContent();
                        int numberOfParts = multipart.getCount();
                        for (int partCount = 0; partCount < numberOfParts; partCount++)
                        {
                            MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(partCount);
                            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
                            {
                                // this part is attachment, saving attachment
                                String fileName = part.getFileName();
                                String attachFiles = "";
                                attachFiles += fileName + ", ";
                                part.saveFile(saveDirectory + File.separator + fileName);
                            }
                        }
                    }
                    //Marking email read
                    email.setFlag(Flags.Flag.SEEN,true);
                }

            inbox.close(true);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
