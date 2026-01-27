# 📊 Live Analytics - Quick Start (5 Minutes)

## ✅ Application URL After Deployment

**Main URL:** `http://localhost:9097`

---

## 🚀 Quick Setup to See Analytics in Action

### 1️⃣ Start the Application
```bash
cd /Users/Ahmed/Downloads/Students-E_voting-
docker-compose up --build
# OR
./run.sh
```

Wait for message: `Started EVotingApplication in X.XXX seconds`

### 2️⃣ Log in as Admin
- **URL:** `http://localhost:9097/login`
- **Username:** `admin`
- **Password:** `admin123`

### 3️⃣ Create an Election (30 seconds)
1. Click **"Manage Elections"**
2. Enter name: `"Student President Election"`
3. Click **"Create Election"**

### 4️⃣ Add Candidates (1 minute)
1. Click **"Manage Candidates"**
2. Click **"Add Candidate"**
3. Select the election you just created
4. Enter name: `"Alice"`
5. Click **"Add Candidate"**
6. Repeat for `"Bob"` and `"Charlie"` (2-3 total)

### 5️⃣ Open Election for Voting
1. Go back to **"Manage Elections"**
2. Find your election
3. Click **"Open"** button
4. Status should change to `OPEN`

### 6️⃣ Cast Some Votes (2 minutes)
**Open a NEW browser tab in PRIVATE/INCOGNITO mode:**

1. Go to `http://localhost:9097/login`
2. Log in as: `student1 / student123`
3. Click on the election name
4. Select a candidate (e.g., "Alice")
5. Click "Vote for This Candidate"
6. Confirm your vote
7. **Logout** (top right)
8. Repeat with different students (student2, student3, etc.)

**Or create test students:**
1. Go to **"Manage Students"** (as admin)
2. Use **"Import Students"** to bulk create them

### 7️⃣ View Live Analytics ⭐
1. Go to **Admin Dashboard**
2. Scroll to **"Live Analytics"** section
3. Select your election from dropdown
4. Click **"View Analytics"**

🎉 **You should now see:**
- ✅ Bar chart with candidate names and vote counts
- ✅ Pie chart showing vote distribution
- ✅ Statistics cards (Total Votes, Leading Candidate, etc.)
- ✅ Candidate rankings with medals 🥇��🥉
- ✅ **Real-time updates every 2 seconds!**

---

## 🔗 Key URLs

| Page | URL |
|------|-----|
| **Login** | `http://localhost:9097/login` |
| **Admin Dashboard** | `http://localhost:9097/admin/dashboard` |
| **Analytics Dashboard** | `http://localhost:9097/admin/analytics/dashboard/{electionId}` |
| **Manage Elections** | `http://localhost:9097/admin/elections` |
| **Manage Candidates** | `http://localhost:9097/admin/candidates` |
| **Manage Students** | `http://localhost:9097/admin/students` |

---

## 🧪 Test Accounts

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Student 1 | `student1` | `student123` |
| Student 2 | `student2` | `student123` |
| Student 3 | `student3` | `student123` |

(Students are auto-created on first application start)

---

## ⚠️ If Analytics Shows "No votes yet"

1. **Did you cast votes?**
   - Log in as student
   - Vote in the OPEN election
   - Verify you see "Vote cast successfully"

2. **Is the election OPEN?**
   - Check "Manage Elections"
   - Status should be "OPEN" (green badge)
   - If not, click the "Open" button

3. **Are there candidates?**
   - Check "Manage Candidates"
   - You should see at least 2-3 candidates

4. **Did you select correct election in analytics?**
   - Dropdown on admin dashboard should match election
   - Click "View Analytics" again

5. **Clear browser cache:**
   - Press `Ctrl+Shift+Del` (Windows) or `Cmd+Shift+Del` (Mac)
   - Clear all cookies and cache
   - Refresh page

---

## 📊 What You Should See

### Analytics Dashboard Contains:

**Top Section:**
- Election name
- Status badge (OPEN/CREATED/CLOSED)
- Live indicator with pulsing dot

**Statistics Cards:**
- Total Votes: `3` (example)
- Candidates: `3`
- Leading Candidate: `Alice`
- Highest Vote Count: `2`

**Charts:**
- Bar chart showing votes per candidate
- Pie chart showing percentage distribution

**Rankings:**
```
🥇 Alice - 2 votes
🥈 Bob - 1 vote
🥉 Charlie - 0 votes
```

**Auto-refresh:**
- Updates every 2 seconds automatically
- No manual refresh needed
- Shows current timestamp

---

## 🐛 If Still Not Working

### Check Server Logs
```bash
tail -f app.log | grep ANALYTICS
```

You should see messages like:
```
[ANALYTICS] getLiveVotes called - ElectionId: 1, Role: ADMIN
[ANALYTICS] Candidates count: 3
[ANALYTICS] Candidate: Alice, Votes: 2
[ANALYTICS] Returning response with 3 candidates, total votes: 2
```

### Verify Database
```bash
mysql -u e_voting -p voting_db
SELECT COUNT(*) FROM vote;  # Should be > 0
SELECT * FROM candidate;    # Should show candidates
```

### Clear Everything & Restart
```bash
docker-compose down -v
docker-compose up --build
```

---

## 🎯 Verification Checklist

Use this to verify everything works:

- [ ] Application starts without errors
- [ ] Can log in as admin
- [ ] Can create election
- [ ] Can add candidates (2+)
- [ ] Can open election
- [ ] Can log in as student and vote
- [ ] Analytics dashboard loads
- [ ] Analytics shows votes in charts
- [ ] Numbers update when new votes added
- [ ] All 4 statistics cards show values

**If all checked:** ✅ **Analytics is working perfectly!**

---

## 💡 Pro Tips

1. **Use Incognito Mode** for student voting to stay logged in as admin in other tab

2. **Keep Admin Dashboard Open** - Refresh it to see live results update

3. **Check Browser Console** (`F12`) for any JavaScript errors

4. **Watch Server Logs** while testing to see debug messages

5. **Cast multiple votes** as different students to see charts update in real-time

---

## 🔍 Advanced: API Testing

Test the API directly:

```bash
# Get election results
curl http://localhost:9097/admin/analytics/api/live-votes/1

# Expected response:
{
  "success": true,
  "data": [
    {"name": "Alice", "votes": 2, "id": 1},
    {"name": "Bob", "votes": 1, "id": 2}
  ],
  "totalVotes": 3,
  "timestamp": 1674814070000
}
```

---

## 📝 Summary

**The live analytics feature:**
- ✅ Records votes when students vote
- ✅ Updates in real-time (every 2 seconds)
- ✅ Shows visual charts and statistics
- ✅ Ranks candidates by vote count
- ✅ Is accessible only to ADMIN users
- ✅ Works for each election separately

**To use it:**
1. Create election → Add candidates → Open election
2. Students vote
3. Admin views analytics → Sees live data!

That's it! 🎉

---

*Updated: January 27, 2026*
