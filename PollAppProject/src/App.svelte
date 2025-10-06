<script>
    import { onMount } from 'svelte';

    // User state
    let username = '';
    let email = '';
    let userId = null;
    let isLoggedIn = false;
    let loginError = false;

    // Poll creation state
    let question = '';
    let options = ['', ''];
    let validUntil;
    let publishedAt;

    // Polls and voting
    let polls = [];
    let selectedVotes = {};

    async function createUser() {
        const user = { username, email };
        const response = await fetch('/users', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user)
        });

        if (response.ok) {
            const createdUser = await response.json();
            userId = createdUser.id; // ensure matches backend field
            username = createdUser.username;
            isLoggedIn = true;
            loginError = false;
        }
    }

    async function loginUser() {
        try {
            const response = await fetch(`/users/login?username=${username}`, {
                method: 'POST'
            });
            if (response.ok) {
                const existingUser = await response.json();
                userId = existingUser.id;
                isLoggedIn = true;
                loginError = false;
            } else {
                loginError = true;
            }
        } catch (error) {
            console.error('Error logging in:', error);
            loginError = true;
        }
    }

    async function logoutUser() {
        await fetch(`/users/logout?username=${username}`, { method: 'POST' });
        isLoggedIn = false;
        userId = null;
        username = '';
        email = '';
    }

    function addOption() {
        options = [...options, ''];
    }

    async function handleSubmit(event) {
        event.preventDefault();
        publishedAt = new Date().toISOString();

        const poll = {
            question,
            options: options.filter(opt => opt.trim() !== ''),
            validUntil: new Date(validUntil).toISOString(),
            publishedAt,
            creatorId: userId
        };

        const response = await fetch('/polls', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(poll)
        });

        if (response.ok) {
            const createdPoll = await response.json();
            polls = [...polls, createdPoll];
            question = '';
            options = ['', ''];
            validUntil = '';
        }
    }

    async function castVote(e, pollId) {
        e.preventDefault();
        const selectedOptionId = selectedVotes[pollId];
        if (!selectedOptionId) return;

        await fetch(`/polls/${pollId}/vote`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId,
                selectedOptionId
            })
        });

        const res = await fetch(`/polls/${pollId}/results`);
        if(res.ok){
            const results = await res.json();
            polls = polls.map(p => p.id === pollId ? { ... p, results} : p)
        }
    }

    function selectOption(pollId, optionId) {
        selectedVotes = {
            ...selectedVotes,
            [pollId]: optionId
        };
    }

    onMount(async () => {
        const response = await fetch('/polls');
        if (response.ok) {
            polls = await response.json();
        }
    });
</script>




<main>
    <h1>Poll Application</h1>

    <!-- Section 1: User -->
    <section class="user-section">
        {#if !isLoggedIn}
            <h2>Welcome</h2>
            <p>Please register or log in to continue.</p>

            <form on:submit|preventDefault={createUser}>
                <input bind:value={username} placeholder="Username" required />
                <input bind:value={email} placeholder="Email" required />
                <button type="submit">Register</button>
            </form>

            <form on:submit|preventDefault={loginUser}>
                <h3>Login</h3>
                <input bind:value={username} placeholder="Username" required />
                {#if loginError}
                    <p class="error-message">User doesn't exist</p>
                {/if}
                <button type="submit">Login</button>
            </form>
        {:else}
            <p>Logged in as: <strong>{username}</strong></p>
            <button on:click={logoutUser}>Logout</button>
        {/if}
    </section>

    <!-- Section 2: Create Poll -->
    {#if isLoggedIn}
        <section class="create-poll-section">
            <h2>Create Poll</h2>
            <form on:submit={handleSubmit}>
                <input bind:value={question} placeholder="Poll question" required />

                {#each options as option, index}
                    <input bind:value={options[index]} placeholder={`Option ${index + 1}`} required />
                {/each}

                <input type="datetime-local" bind:value={validUntil} required />
                <button type="button" on:click={addOption}>+ Add Option</button>
                <button type="submit">Create Poll</button>
            </form>
        </section>

        <!-- Section 3: Cast Vote -->
        <section class="vote-section">
            <h2>Vote on Polls</h2>
            {#each polls as poll}
                <div class="poll-card">
                    <h3>{poll.question}</h3>
                    <form on:submit={(e) => castVote(e, poll.id)}>
                        {#each poll.options as option}
                            <label>
                                <input
                                        type="radio"
                                        name={`poll-${poll.id}`}
                                        value={option.id}
                                        checked={selectedVotes[poll.id] === option.id}
                                        on:change={() => selectOption(poll.id, option.id)}
                                />
                                {option.caption}
                                {#if poll.results}
                                    <span class="vote-count">({poll.results[option.caption] || 0})</span>
                                {/if}
                            </label>
                        {/each}
                        <button type="submit">Cast Vote</button>
                    </form>
                </div>
            {/each}
        </section>
    {/if}
</main>

