package utils;

import models.JavaClass;
import models.JavaPackage;
import models.history.JavaClassHistory;
import models.history.JavaPackageHistory;

import java.io.Serializable;
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
    public JavaPackageHistory toHistory(String version, JavaPackage pkg) {
        // empty lists that will be filled with the JPH and JCH children of the current package
        List<JavaPackageHistory> jpChildren = new ArrayList<>();
        List<JavaClassHistory> jcChildren = new ArrayList<>();

        // the list of JavaPackage for the different version of this pkg
        Map<String, JavaPackage> jpHistory = new HashMap<>();

        JavaPackageHistory tempPackageHistory;

        // recursively call toHistory on children packages
        for (JavaPackage child : pkg.getChildPackages()) {
            tempPackageHistory = toHistory(version, child);
            jpChildren.add(tempPackageHistory);
        }

        // make children classes into JCHs
        for (JavaClass cls : pkg.getClasses()) {
            JavaClassHistory jcHistory;
            // if we already have other versions for
            // this specific class(name)
            if (!classesMap.containsKey(cls.getName())) {
                jcHistory = new JavaClassHistory(cls.getName(), new ArrayList<>());
                jcHistory.addClassHistory(cls);
                classesMap.put(cls.getName(), jcHistory);
                jcChildren.add(jcHistory);
            } else {
                jcHistory = classesMap.get(cls.getName());
                jcHistory.addClassHistory(cls);
            }
        }

        jpHistory.put(version, pkg);
        // if there already exists a history for this specific package(name)
        if (!packagesMap.containsKey(pkg.getName())) {
            // create the new JPH, put it in the map and return it
            tempPackageHistory = new JavaPackageHistory(pkg.getName(), jpChildren, jcChildren, jpHistory);
            packagesMap.put(pkg.getName(), tempPackageHistory);
            return tempPackageHistory;

        } else {
            // update the JPH and return it
            tempPackageHistory = packagesMap.get(pkg.getName());
            tempPackageHistory.addJcChildren(jcChildren);
            tempPackageHistory.addOrUpdateJpChildren(jpChildren);
            tempPackageHistory.addPackageHistory(version, pkg);
            return tempPackageHistory;
        }
    }
}
