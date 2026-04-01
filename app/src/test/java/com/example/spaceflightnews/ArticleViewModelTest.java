package com.example.spaceflightnews;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spaceflightnews.data.Article;
import com.example.spaceflightnews.repository.ArticleRepository;
import com.example.spaceflightnews.repository.RepositoryCallback;
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
        viewModel = new ArticleViewModel(mockApplication, mockRepository);
    }

    @Test
    public void fetchArticles_shouldInvokeRepositorySync() {
        viewModel.fetchArticles();
        verify(mockRepository, Mockito.times(1)).syncArticles(any(RepositoryCallback.class));
    }

    @Test
    public void searchArticles_updatesLiveData() {
        MutableLiveData<List<Article>> mockLiveData = new MutableLiveData<>();
        List<Article> articles = new ArrayList<>();
        articles.add(new Article(1, "News Title", "https://example.com/image.jpg", "Summary", "2023-08-01", "https://example.com"));
        mockLiveData.setValue(articles);

        when(mockRepository.search(eq("NASA"), any(RepositoryCallback.class))).thenReturn(mockLiveData);

        LiveData<List<Article>> result = viewModel.searchArticles("NASA");

        assertNotNull(result.getValue());
        assertEquals(1, result.getValue().size());
        assertEquals("News Title", result.getValue().get(0).title);

        verify(mockRepository, Mockito.times(1)).search(eq("NASA"), any(RepositoryCallback.class));
    }

    @Test
    public void searchArticles_emptyQuery_returnsEmptyList() {

        String emptyQuery = "";
        MutableLiveData<List<Article>> emptyData = new MutableLiveData<>();
        emptyData.setValue(new ArrayList<>());

        when(mockRepository.search(eq(emptyQuery), any(RepositoryCallback.class))).thenReturn(emptyData);

        LiveData<List<Article>> result = viewModel.searchArticles(emptyQuery);

        assertNotNull(result.getValue());
        assertTrue(result.getValue().isEmpty());
        verify(mockRepository).search(eq(emptyQuery), any(RepositoryCallback.class));
    }

    @Test
    public void searchArticles_noMatches_returnsEmptyLiveData() {
        String query = "UnknownTopic";
        MutableLiveData<List<Article>> emptyLiveData = new MutableLiveData<>();
        emptyLiveData.setValue(new ArrayList<>());

        when(mockRepository.search(eq(query), any(RepositoryCallback.class)))
                .thenReturn(emptyLiveData);
        LiveData<List<Article>> result = viewModel.searchArticles(query);
        assertEquals(0, result.getValue().size());

        verify(mockRepository).search(eq(query), any(RepositoryCallback.class));
    }

    @Test
    public void getArticleById_validId_returnsCorrectArticle() {
        int articleId = 101;
        Article mockArticle = new Article(articleId, "SpaceX Launch", "img.jpg", "Summary", "2024", "url");
        MutableLiveData<Article> liveData = new MutableLiveData<>();
        liveData.setValue(mockArticle);

        when(mockRepository.getArticleById(articleId)).thenReturn(liveData);

        LiveData<Article> result = viewModel.getArticleById(articleId);

        assertNotNull(result.getValue());
        assertEquals(articleId, result.getValue().id);
        assertEquals("SpaceX Launch", result.getValue().title);
    }

    @Test
    public void getRecentArticles_whenCalled_invokesRepositoryRecentMethod() {
        MutableLiveData<List<Article>> recentData = new MutableLiveData<>();
        recentData.setValue(new ArrayList<>()); // Simulación de lista vacía o con datos

        when(mockRepository.getRecentArticles()).thenReturn(recentData);

        viewModel.getRecentArticles();
        verify(mockRepository, Mockito.times(1)).getRecentArticles();
    }
}