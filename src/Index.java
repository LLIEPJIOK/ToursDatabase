import java.io.*;
import java.util.*;
import java.util.zip.*;

// comparators for strings
// right order
class KeyStringComp implements Comparator<String> {
    public int compare(String o1, String o2) {
        return o1.compareTo(o2);
    }
}

// reverse order
class KeyStringCompReverse implements Comparator<String> {
    public int compare(String o1, String o2) {
        return o2.compareTo(o1);
    }
}

// comparators for integers
// right order
class KeyIntComp implements Comparator<String> {
    public int compare(String o1, String o2) {
        Integer a1 = Integer.parseInt(o1);
        Integer a2 = Integer.parseInt(o2);
        return a1.compareTo(a2);
    }
}

// reverse order
class KeyIntCompReverse implements Comparator<String> {
    public int compare(String o1, String o2) {
        Integer a1 = Integer.parseInt(o1);
        Integer a2 = Integer.parseInt(o2);
        return a2.compareTo(a1);
    }
}

// interface for unique and multiple keys
interface IndexBase {
    // getting keys
    String[] getKeys(Comparator<String> comp);

    // putting keys
    void put(String key, long value);

    // checking containing
    boolean contains(String key);

    // getting positions
    Long[] get(String key);
}

// Unique keys
class IndexOne2One implements Serializable, IndexBase {
    private static final long serialVersionUID = 1L;

    // map for positions
    private TreeMap<String, Long> map;

    // constructor
    public IndexOne2One() {
        map = new TreeMap<String, Long>();
    }

    // IndexBase staff
    public String[] getKeys(Comparator<String> comp) {
        String[] result = map.keySet().toArray(new String[0]);
        Arrays.sort(result, comp);
        return result;
    }

    public void put(String key, long value) {
        map.put(key, value);
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public Long[] get(String key) {
        long pos = map.get(key);
        return new Long[] { pos };
    }
}

// Multiple keys
class IndexOne2N implements Serializable, IndexBase {
    private static final long serialVersionUID = 1L;

    // map for positions
    private TreeMap<String, Vector<Long>> map;

    // constructor
    public IndexOne2N() {
        map = new TreeMap<String, Vector<Long>>();
    }

    // IndexBase staff
    public String[] getKeys(Comparator<String> comp) {
        String[] result = map.keySet().toArray(new String[0]);
        Arrays.sort(result, comp);
        return result;
    }

    public void put(String key, long value) {
        Vector<Long> arr = map.get(key);
        if (arr == null) {
            arr = new Vector<Long>();
        }
        arr.add(value);
        map.put(key, arr);
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public Long[] get(String key) {
        return map.get(key).toArray(new Long[0]);
    }
}

// class for indexing throw tours
public class Index implements Serializable {
    private static final long serialVersionUID = 1L;

    // objects for indexing for names/fullNames/days
    IndexOne2One names;
    IndexOne2N fullNames;
    IndexOne2N days;

    // constructor
    public Index() {
        names = new IndexOne2One();
        fullNames = new IndexOne2N();
        days = new IndexOne2N();
    }

    // checking unique keys
    public void test(Tour tour) throws KeyNotUniqueException {
        if (names.contains(tour.tourName)) {
            throw new KeyNotUniqueException(tour.tourName);
        }
    }

    // putting tour
    public void put(Tour tour, long value) throws KeyNotUniqueException {
        names.put(tour.tourName, value);
        fullNames.put(tour.clientName, value);
        days.put(String.valueOf(tour.days), value);
    }

    // loading index
    public static Index load(String name) throws IOException, ClassNotFoundException {
        Index obj = null;
        try {
            FileInputStream file = new FileInputStream(name);
            try (ZipInputStream zis = new ZipInputStream(file)) {
                ZipEntry zen = zis.getNextEntry();
                if (!zen.getName().equals(Buffer.zipEntryName)) {
                    throw new IOException("Invalid block format");
                }
                try (ObjectInputStream ois = new ObjectInputStream(zis)) {
                    obj = (Index) ois.readObject();
                }
                zis.close();
            }
            file.close();
        } catch (FileNotFoundException e) {
            obj = new Index();
        }
        return obj;
    }

    // saving index
    public void save(String name) throws IOException {
        FileOutputStream file = new FileOutputStream(name);
        try (ZipOutputStream zos = new ZipOutputStream(file)) {
            zos.putNextEntry(new ZipEntry(Buffer.zipEntryName));
            zos.setLevel(ZipOutputStream.DEFLATED);
            try (ObjectOutputStream oos = new ObjectOutputStream(zos)) {
                oos.writeObject(this);
                oos.flush();
                zos.closeEntry();
                zos.flush();
            }
            zos.close();
        }
        file.close();
    }
}
