package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
@Repository
public class PostRepository {
    private final ConcurrentHashMap<Long, Post> postCollection;
    AtomicLong countPosts = new AtomicLong(0L);

    public PostRepository() {
        this.postCollection = new ConcurrentHashMap<>();
    }

    public List<Post> all() {
        return postCollection.values().stream().filter(x -> x.getRemoved().equals(false)).collect(Collectors.toCollection(ArrayList::new));
    }


    public Optional<Post> getById(long id) throws NotFoundException {
        if (postCollection.get(id).getRemoved().equals(true)) {
            throw new NotFoundException("Пост уже удален!");
        }
        return Optional.ofNullable(postCollection.get(id));
    }

    public Post save(Post post) throws NotFoundException {
        if (post.getId() == 0) {
            countPosts.incrementAndGet();
            post.setId(countPosts.get());
            postCollection.put(countPosts.get(), post);
            return postCollection.get(countPosts.get());
        } else {
            Post postByIdFromCollection = postCollection.get(post.getId());
            if (postByIdFromCollection.getRemoved().equals(true)) {
                throw new NotFoundException("Пост уже удален!");
            }
            postByIdFromCollection.setContent(post.getContent());
            return postByIdFromCollection;
        }
    }

    public void removeById(long id) {
        postCollection.get(id).setRemoved(true);
    }
}