package org.dei.perla.web.aggre.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;


public class SerializableTest {
    public static final Object clone(Serializable in) {
        try {
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            ObjectOutputStream outStream = new ObjectOutputStream(byteOutStream);
            outStream.writeObject(in);
            ByteArrayInputStream byteInStream =
                new ByteArrayInputStream(byteOutStream.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteInStream);
            return inStream.readObject();
        } catch (OptionalDataException e) {
         throw new RuntimeException("Optional data found. " + e.getMessage()); //$NON-NLS-1$
        } catch (StreamCorruptedException e) {
         throw new RuntimeException("Serialized object got corrupted. " + e.getMessage()); //$NON-NLS-1$
        } catch (ClassNotFoundException e) {
         throw new RuntimeException("A class could not be found during deserialization. " + e.getMessage()); //$NON-NLS-1$
        } catch (NotSerializableException ex) {
            ex.printStackTrace();
         throw new IllegalArgumentException("Object is not serializable: " + ex.getMessage()); //$NON-NLS-1$
        } catch (IOException e) {
         throw new RuntimeException("IO operation failed during serialization. " + e.getMessage()); //$NON-NLS-1$
        }
    }
} 