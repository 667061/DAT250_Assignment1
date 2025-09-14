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
            userId = createdUser.userID;
            isLoggedIn = true;
        }
    }
    function toISOStringWithZ(datetimeLocal) {
        const date = new Date(datetimeLocal);
        return date.toISOString(); // Converts to "2025-10-25T19:02:00.000Z"
    }


    async function loginUser() {
        try {
            const response = await fetch(`/users/login?username=${username}`);
            if (response.ok) {
                const existingUser = await response.json();
                userId = existingUser.userID;
                isLoggedIn = true;
                loginError = false; // clear error
            } else {
                loginError = true; // show error
            }
        } catch (error) {
            console.error('Error logging in:', error);
            loginError = true;
        }
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
            validUntil: toISOStringWithZ(validUntil),
            publishedAt: new Date().toISOString(),
            creator: { id: userId }
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

    async function castVote(event, pollId) {
        event.preventDefault();
        const selected = selectedVotes[pollId] || [];

        const vote = {
            userId,
            selectedOptions: selected
        };

        const response = await fetch(`/polls/${pollId}/vote`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(vote)
        });

        if (response.ok) {
            alert('Vote cast successfully!');
        }
    }

    function toggleOption(pollId, caption) {
        const current = selectedVotes[pollId] || [];
        selectedVotes[pollId] = current.includes(caption)
            ? current.filter(o => o !== caption)
            : [...current, caption];
    }


    onMount(async () => {
        const response = await fetch('/polls');
        if (response.ok) {
            polls = await response.json();
        }
    });
</script>




<main>
    <h1>Poll application</h1>
    <!-- Section 1: User -->
    <section class="user-section">
        <h2>User</h2>
        {#if !isLoggedIn}
            <form on:submit|preventDefault={createUser}>
                <input bind:value={username} placeholder="Username" />
                <input bind:value={email} placeholder="Email" />
                <button type="submit">Create User</button>
            </form>

            <form on:submit|preventDefault={loginUser}>
                <h2>Login</h2>
                <input bind:value={username} placeholder="Username" />

                {#if loginError}
                    <p class="error-message">User doesn't exist</p>
                {/if}

                <button type="submit">Login</button>
            </form>

        {:else}
            <p>Logged in as: <strong>{username}</strong></p>
        {/if}
    </section>

    <!-- Section 2: Create Poll -->
    {#if isLoggedIn}
        <section class="create-poll-section">
            <h2>Create Poll</h2>
            <form on:submit={handleSubmit}>
                <input bind:value={question} placeholder="Poll question" />

                {#each options as option, index}
                    <input bind:value={options[index]} placeholder={`Option ${index + 1}`} />
                {/each}

                <input type="datetime-local" bind:value={validUntil} />
                <button type="button" on:click={addOption}>+ Add Option</button>
                <button type="submit">Create Poll</button>
            </form>
        </section>
    {/if}

    <!-- Section 3: Cast Vote -->
    <section class="vote-section">
        <h2>Vote on polls</h2>
        {#each polls as poll}
            <div class="poll-card">
                <h3>{poll.question}</h3>
                <form on:submit={(e) => castVote(e, poll.id)}>
                    {#each poll.options as option}
                        <label>
                            <input
                                    type="checkbox"
                                    checked={selectedVotes[poll.id]?.includes(option.caption)}
                                    on:change={() => toggleOption(poll.id, option.caption)}
                            />
                            {option.caption} <!--<span class="vote-count">({option.voteCount} votes)</span> For future, display number of votes-->
                        </label>
                    {/each}
                    <button type="submit">Cast Vote</button>
                </form>
            </div>
        {/each}
    </section>
</main>

