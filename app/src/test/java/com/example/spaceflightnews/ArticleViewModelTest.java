package com.example.spaceflightnews;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spaceflightnews.data.Article;
import com.example.spaceflightnews.repository.ArticleRepository;
import com.example.spaceflightnews.ui.ArticleViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ArticleViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Application mockApplication; // AndroidViewModel usa Application

    @Mock
    private ArticleRepository mockRepository;

    private ArticleViewModel viewModel;

    @Before
    public void setup() {
        // Simulamos el comportamiento del repositorio antes de cada test
        viewModel = new ArticleViewModel(mockApplication, mockRepository);
    }

    @Test
    public void sync_shouldInvokeRepositorySync() {
        // Act (Acción)
        viewModel.sync();

        // Assert (Verificación)
        // Verificamos que el ViewModel delegó la tarea al repositorio
        Mockito.verify(mockRepository, Mockito.times(1)).syncArticles();
    }

    @Test
    public void searchArticles_updatesLiveData() {
        // 1. Arrange: Preparamos datos falsos
        MutableLiveData<List<Article>> mockLiveData = new MutableLiveData<>();
        List<Article> articles = new ArrayList<>();
        articles.add(new Article(1, "News Title", "https://example.com/image.jpg", "Summary", "2023-08-01"));
        mockLiveData.setValue(articles);

        // 2. Mock: Cuando el repo busque "NASA", que devuelva nuestro LiveData
        Mockito.when(mockRepository.search("NASA")).thenReturn(mockLiveData);

        // 3. Act: Ejecutamos la acción en el ViewModel
        LiveData<List<Article>> result = viewModel.searchArticles("NASA");

        // 4. Assert: Verificamos que el resultado sea el esperado
        assertNotNull(result.getValue());
        assertEquals(1, result.getValue().size());
        assertEquals("News Title", result.getValue().get(0).title);

        // Verificamos que el método del repositorio fue llamado exactamente una vez
        Mockito.verify(mockRepository, Mockito.times(1)).search("NASA");
    }

    @Test
    public void searchArticles_emptyQuery_returnsEmptyList() {
        // Arrange
        String emptyQuery = "";
        MutableLiveData<List<Article>> emptyData = new MutableLiveData<>();
        emptyData.setValue(new ArrayList<>());

        Mockito.when(mockRepository.search(emptyQuery)).thenReturn(emptyData);

        // Act
        LiveData<List<Article>> result = viewModel.searchArticles(emptyQuery);

        // Assert
        assertNotNull(result.getValue());
        assertTrue(result.getValue().isEmpty());
        Mockito.verify(mockRepository).search(emptyQuery);
    }

    @Test
    public void searchArticles_noMatches_returnsEmptyLiveData() {
        // Arrange
        String query = "UnknownTopic";
        MutableLiveData<List<Article>> emptyLiveData = new MutableLiveData<>();
        emptyLiveData.setValue(new ArrayList<>());

        Mockito.when(mockRepository.search(query)).thenReturn(emptyLiveData);

        // Act
        LiveData<List<Article>> result = viewModel.searchArticles(query);

        // Assert
        assertEquals(0, result.getValue().size());
    }

    @Test
    public void getArticleById_validId_returnsCorrectArticle() {
        // Arrange
        int articleId = 101;
        Article mockArticle = new Article(articleId, "SpaceX Launch", "img.jpg", "Summary", "2024");
        MutableLiveData<Article> liveData = new MutableLiveData<>();
        liveData.setValue(mockArticle);

        Mockito.when(mockRepository.getArticleById(articleId)).thenReturn(liveData);

        // Act
        LiveData<Article> result = viewModel.getArticleById(articleId);

        // Assert
        assertNotNull(result.getValue());
        assertEquals(articleId, result.getValue().id);
        assertEquals("SpaceX Launch", result.getValue().title);
    }

    @Test
    public void getRecentArticles_whenCalled_invokesRepositoryRecentMethod() {
        // Arrange
        MutableLiveData<List<Article>> recentData = new MutableLiveData<>();
        recentData.setValue(new ArrayList<>()); // Simulación de lista vacía o con datos

        Mockito.when(mockRepository.getRecentArticles()).thenReturn(recentData);

        // Act
        viewModel.getRecentArticles();

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1)).getRecentArticles();
    }
}