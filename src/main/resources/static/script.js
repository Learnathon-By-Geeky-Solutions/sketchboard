function getAllPosts() {
    fetch('/posts')
        .then(response => response.json())
        .then(data => {
            const container = document.getElementById('post-container');
            container.innerHTML = `
                <table class="post-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Description</th>
                            <th>Location</th>
                            <th>Date</th>
                            <th>Time</th>
                            <th>Category</th>
                            <th>Status</th>
                            <th>Range</th>
                            <th>Upload Time</th>
                            <th>Last Updated Time</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${data.map(post => `
                            <tr>
                                <td>${post.id}</td>
                                <td>${post.title}</td>
                                <td>${post.description}</td>
                                <td>${post.location}</td>
                                <td>${post.date}</td>
                                <td>${post.time}</td>
                                <td>${post.category}</td>
                                <td>${post.status}</td>
                                <td>${post.range}</td>
                                <td>${post.uploadTime}</td>
                                <td>${post.lastUpdatedTime}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            `;
        })
        .catch(error => console.error('Error fetching posts:', error));
}

function openGetPostModal() {
    document.getElementById('getPostModal').style.display = "block";
    document.getElementById('main-content').classList.add('blur');
    document.getElementById('content').classList.add('blur');
}

function closeGetPostModal() {
    document.getElementById('getPostModal').style.display = "none";
    document.getElementById('main-content').classList.remove('blur');
    document.getElementById('content').classList.remove('blur');
}

function getPostById(event) {
    event.preventDefault();
    const postId = document.getElementById('postId').value;
    fetch(`/posts/${postId}`)
        .then(response => response.json())
        .then(post => {
            const container = document.getElementById('post-container');
            container.innerHTML = `
                <table class="post-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Description</th>
                            <th>Location</th>
                            <th>Date</th>
                            <th>Time</th>
                            <th>Category</th>
                            <th>Status</th>
                            <th>Range</th>
                            <th>Upload Time</th>
                            <th>Last Updated Time</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>${post.id}</td>
                            <td>${post.title}</td>
                            <td>${post.description}</td>
                            <td>${post.location}</td>
                            <td>${post.date}</td>
                            <td>${post.time}</td>
                            <td>${post.category}</td>
                            <td>${post.status}</td>
                            <td>${post.range}</td>
                            <td>${post.uploadTime}</td>
                            <td>${post.lastUpdatedTime}</td>
                        </tr>
                    </tbody>
                </table>
            `;
            closeGetPostModal();
        })
        .catch(error => console.error('Error fetching post:', error));
}

function openDeletePostModal() {
    document.getElementById('deletePostModal').style.display = "block";
    document.getElementById('main-content').classList.add('blur');
    document.getElementById('content').classList.add('blur');
}

function closeDeletePostModal() {
    document.getElementById('deletePostModal').style.display = "none";
    document.getElementById('main-content').classList.remove('blur');
    document.getElementById('content').classList.remove('blur');
}

function deletePostById(event) {
    event.preventDefault();
    const postId = document.getElementById('deletePostId').value;
    fetch(`/posts/${postId}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                alert('Post deleted successfully');
                closeDeletePostModal();
                getAllPosts(); // Refresh the post list
            } else {
                alert('Failed to delete post');
            }
        })
        .catch(error => console.error('Error deleting post:', error));
}

function openCreatePostModal() {
    document.getElementById('createPostModal').style.display = "block";
    document.getElementById('main-content').classList.add('blur');
    document.getElementById('content').classList.add('blur');
}

function closeCreatePostModal() {
    document.getElementById('createPostModal').style.display = "none";
    document.getElementById('main-content').classList.remove('blur');
    document.getElementById('content').classList.remove('blur');
}

function createPost(event) {
    event.preventDefault();
    const post = {
        title: document.getElementById('title').value,
        description: document.getElementById('description').value,
        location: document.getElementById('location').value,
        date: document.getElementById('date').value,
        time: document.getElementById('time').value,
        category: document.getElementById('category').value,
        status: document.getElementById('status').value,
        range: document.getElementById('range').value
    };
    fetch('/posts', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(post)
    })
        .then(response => {
            if (response.ok) {
                alert('Post created successfully');
                closeCreatePostModal();
                getAllPosts(); // Refresh the post list
            } else {
                alert('Failed to create post');
            }
        })
        .catch(error => console.error('Error creating post:', error));
}

function openUpdatePostModal() {
    document.getElementById('updatePostModal').style.display = "block";
    document.getElementById('main-content').classList.add('blur');
    document.getElementById('content').classList.add('blur');
}

function closeUpdatePostModal() {
    document.getElementById('updatePostModal').style.display = "none";
    document.getElementById('main-content').classList.remove('blur');
    document.getElementById('content').classList.remove('blur');
}

function updatePost(event) {
    event.preventDefault();
    const postId = document.getElementById('updatePostId').value;
    fetch(`/posts/${postId}`)
        .then(response => response.json())
        .then(existingPost => {
            const updatedPost = {
                title: document.getElementById('updateTitle').value || existingPost.title,
                description: document.getElementById('updateDescription').value || existingPost.description,
                location: document.getElementById('updateLocation').value || existingPost.location,
                date: document.getElementById('updateDate').value || existingPost.date,
                time: document.getElementById('updateTime').value || existingPost.time,
                category: document.getElementById('updateCategory').value || existingPost.category,
                status: document.getElementById('updateStatus').value || existingPost.status,
                range: document.getElementById('updateRange').value || existingPost.range
            };
            fetch(`/posts/${postId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(updatedPost)
            })
                .then(response => {
                    if (response.ok) {
                        alert('Post updated successfully');
                        closeUpdatePostModal();
                        getAllPosts(); // Refresh the post list
                    } else {
                        alert('Failed to update post');
                    }
                })
                .catch(error => console.error('Error updating post:', error));
        })
        .catch(error => console.error('Error fetching existing post:', error));
}

function searchPosts() {
    const query = document.getElementById('searchQuery').value;
    fetch(`/search-posts?query=${query}`)
        .then(response => response.json())
        .then(data => {
            const container = document.getElementById('post-container');
            container.innerHTML = `
                <table class="post-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Description</th>
                            <th>Location</th>
                            <th>Date</th>
                            <th>Time</th>
                            <th>Category</th>
                            <th>Status</th>
                            <th>Range</th>
                            <th>Upload Time</th>
                            <th>Last Updated Time</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${data.map(post => `
                            <tr>
                                <td>${post.id}</td>
                                <td>${post.title}</td>
                                <td>${post.description}</td>
                                <td>${post.location}</td>
                                <td>${post.date}</td>
                                <td>${post.time}</td>
                                <td>${post.category}</td>
                                <td>${post.status}</td>
                                <td>${post.range}</td>
                                <td>${post.uploadTime}</td>
                                <td>${post.lastUpdatedTime}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            `;
        })
        .catch(error => console.error('Error searching posts:', error));
}