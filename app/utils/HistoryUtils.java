package utils;

import models.JavaClass;
import models.JavaPackage;
import models.history.JavaClassHistory;
import models.history.JavaPackageHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class that given root JavaPackages for a version of a system, it returns JavaPackageHistories for the various
 * versions of the system
 */
public class HistoryUtils {

    private Map<String, JavaPackageHistory> packagesMap;
    private Map<String, JavaClassHistory> classesMap;

    public HistoryUtils() {
        this.packagesMap = new HashMap<>();
        this.classesMap = new HashMap<>();
    }


    /**
     * @param pkg the JavaPackage that we want to make into, or add to a JavaPackageHistory
     * @return the JavaPackageHistory
     */
    public JavaPackageHistory toHistory(JavaPackage pkg) {
        // empty lists that will be filled with the JPH and JCH children of the current package
        List<JavaPackageHistory> jpChildren = new ArrayList<>();
        List<JavaClassHistory> jcChildren = new ArrayList<>();

        // the list of JavaPackage for the different version of this pkg
        List<JavaPackage> jpHistory = new ArrayList<>();

        // recursively call toHistory on children packages
        for (JavaPackage child : pkg.getChildPackages()) {
            jpChildren.add(toHistory(child));
        }

        // make children classes into JCHs
        for (JavaClass cls : pkg.getClasses()) {
            JavaClassHistory jcHistory;
            // if we already have other versions for this specific class(name)
            if (!classesMap.containsKey(cls.getName())) {
                jcHistory = new JavaClassHistory(cls.getName(), new ArrayList<>());
                classesMap.put(cls.getName(), jcHistory);
            } else {
                jcHistory = classesMap.get(cls.getName());
                jcHistory.addClassHistory(cls);
            }

            jcChildren.add(jcHistory);
        }

        jpHistory.add(pkg);

        // if there already exists a history for this specific package(name)
        if (!packagesMap.containsKey(pkg.getName())) {
            // create the new JPH, put it in the map and return it
            JavaPackageHistory temp = new JavaPackageHistory(pkg.getName(), jpChildren, jcChildren, jpHistory);
            packagesMap.put(pkg.getName(), temp);
            return temp;

        } else {
            // update the JPH and return it
            JavaPackageHistory temp = packagesMap.get(pkg.getName());
            temp.addJcChildren(jcChildren);
            temp.addJpChildren(jpChildren);
            temp.addPackageHistory(pkg);
            return temp;
        }
    }
}
