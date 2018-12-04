package com.kindhomeless.wa.walletassistant.repo.storage;

import java.util.HashMap;
import java.util.Map;

public class RepositoryManager {
    private static final RepositoryManager ourInstance = new RepositoryManager();

    private Map<Class<?>, Object> repositoryByClass = new HashMap<>();
    private Map<Class<?>, Object> repositoryByClassForTests = new HashMap<>();

    public static RepositoryManager getInstance() {
        return ourInstance;
    }

    private RepositoryManager() {
    }

    /**
     * @return the repository proxy that corresponds to the given class
     */
    public <T> T getRepository(Class<T> repo) {
        return doGetRepo(repo, repositoryByClass, this::initializeRepositoryByClassMap);
    }

    /**
     * @return the repository proxy that corresponds to the given class. Should be used only for
     * test purposes
     */
    public <T> T getRepositoryForTest(Class<T> repo) {
        return doGetRepo(repo, repositoryByClassForTests,
                this::initializeRepositoryByClassMapForTests);

    }

    private <T> T doGetRepo(
            Class<T> repo,
            Map<Class<?>, Object> repoByClass,
            Runnable repoByClassInitFunction) {

        if (repoByClass.isEmpty()) {
            repoByClassInitFunction.run();
        }
        Object retVal = repoByClass.get(repo);
        if (retVal == null) {
            throw new UnknownRepositoryException("Cannot find internal repository for " + repo.getName());
        }
        return repo.cast(repoByClass.get(repo));
    }

    private void initializeRepositoryByClassMap() {
        // TODO update to real repo when it's implemented
        repositoryByClass.put(PaymentPlaceRepo.class, new PaymentPlaceRepoMock());
    }


    private void initializeRepositoryByClassMapForTests() {
        repositoryByClassForTests.put(PaymentPlaceRepo.class, new PaymentPlaceRepoMock());
    }
}
