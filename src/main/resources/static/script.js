let currentAction = '';

function getAllPosts() {
    fetch('/posts', { method: 'GET' })
        .then(response => response.json())
        .then(data => {
            console.log(data); // Log data to check if it is being fetched correctly
            displayResults(data);
        })
        .catch(error => console.error("Error fetching posts:", error));
}
function searchPosts() {
    const query = document.getElementById('searchQuery').value;
    fetch(`/search-posts?query=${encodeURIComponent(query)}`, { method: 'GET' })
        .then(response => response.json())
        .then(data => {
            console.log(data); // Log data to check if it is being fetched correctly
            displayResults(data);
        })
        .catch(error => console.error("Error searching posts:", error));
}
function showForm(action) {
    currentAction = action;
    const formContainer = document.getElementById('formContainer');
    const formTitle = document.getElementById('formTitle');
    const idField = document.getElementById('idField');
    const fields = document.getElementById('fields');
    const resultContainer = document.getElementById('resultContainer');

    formContainer.style.display = 'block';
    resultContainer.innerHTML = ''; // Clear previous results

    switch (action) {
        case 'getPostById':
            formTitle.innerText = 'Get Post By ID';
            idField.style.display = 'block';
            fields.classList.add('hidden');
            break;
        case 'deletePost':
            formTitle.innerText = 'Delete Post By ID';
            idField.style.display = 'block';
            fields.classList.add('hidden');
            break;
        case 'createPost':
            formTitle.innerText = 'Create Post';
            idField.style.display = 'none';
            fields.classList.remove('hidden');
            break;
        case 'updatePost':
            formTitle.innerText = 'Update Post By ID';
            idField.style.display = 'block';
            fields.classList.remove('hidden');
            break;
    }
}

function executeAction() {
    const id = document.getElementById('id').value;
    const postData = {
        title: document.getElementById('title').value,
        description: document.getElementById('description').value,
        location: document.getElementById('location').value,
        date: document.getElementById('date').value,
        time: document.getElementById('time').value,
        category: document.getElementById('category').value,
        status: document.getElementById('status').value,
        range: document.getElementById('range').value,
    };

    switch (currentAction) {
        case 'getPostById':
            fetch(`/posts/${id}`, { method: 'GET' })
                .then(response => response.json())
                .then(data => displayResults([data])) // Wrap single post in array for consistency
                .catch(error => console.error("Error fetching post by ID:", error));
            break;
        case 'deletePost':
            fetch(`/posts/${id}`, { method: 'DELETE' })
                .then(() => alert('Post deleted successfully!'))
                .catch(error => console.error("Error deleting post:", error));
            break;
        case 'createPost':
            fetch('/posts', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(postData)
            })
                .then(response => response.json())
                .then(() => {
                    alert('Post created successfully!');
                    getAllPosts(); // Refresh the list of posts
                })
                .catch(error => console.error("Error creating post:", error));
            break;
        case 'updatePost':
            fetch(`/posts/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(postData)
            })
                .then(response => response.json())
                .then(() => alert('Post updated successfully!'))
                .catch(error => console.error("Error updating post:", error));
            break;
    }
}

function displayResults(data) {
    const resultContainer = document.getElementById('resultContainer');
    resultContainer.innerHTML = '';

    if (!data || data.length === 0) {
        resultContainer.innerHTML = '<p>No posts found.</p>';
        return;
    }

    const table = document.createElement('table');
    table.classList.add('result-table');

    const thead = document.createElement('thead');
    const headerRow = document.createElement('tr');
    const headers = ['ID', 'Title', 'Description', 'Location', 'Date', 'Time', 'Category', 'Status', 'Range'];

    headers.forEach(header => {
        const th = document.createElement('th');
        th.innerText = header;
        headerRow.appendChild(th);
    });

    thead.appendChild(headerRow);
    table.appendChild(thead);

    const tbody = document.createElement('tbody');
    data.forEach(post => {
        const row = document.createElement('tr');

        headers.forEach(key => {
            const td = document.createElement('td');
            td.innerText = post[key.toLowerCase()] || '';
            row.appendChild(td);
        });

        tbody.appendChild(row);
    });

    table.appendChild(tbody);
    resultContainer.appendChild(table);
}
