# 🎥 SQBase Demo Script

**Total Time: ~2:00 mins**

### Step 1: Connection & Overview (0:00 - 0:20)
*Action:* Launch SQBase, displaying the custom "SQ" monogram splash screen. The workspace automatically connects to a local PostgreSQL instance.
*Talking Point:* "Welcome to SQBase—the smartest database workspace I've engineered. I built this to solve the fractured workflow developers face between querying, tuning, and monitoring. Here's my primary data source instantly loaded via native JDBC."

### Step 2: AI SQL Assistant (0:20 - 0:50)
*Action:* Open a new SQL tab. In the natural language bar at the top, type: _"show me top 10 customers who spent more than $500 last month."_ Press enter. 
*Talking Point:* "Writing repetitive SQL is a time-sink. I integrated a dual OpenAI/Ollama engine that reads my exact database schema in real-time. Notice how it generates a perfect JOIN query using realistic table names. It even provided an automated execution plan explanation below."

### Step 3: Real-Time DB Dashboard (0:50 - 1:10)
*Action:* Click the 'Dashboard' tab on the sidebar. Show the live updating graphs for active connections and query latency.
*Talking Point:* "I built this async dashboard because backend engineers shouldn't need a separate APM to see bottleneck queries. It aggressively polls system tables. Look how this active query lags past 3 seconds—the threshold monitor instantly highlights the node in red."

### Step 4: Data Visualizer (1:10 - 1:30)
*Action:* Execute the previous AI-generated query. Result set opens. Click the magic 'Visualize' button.
*Talking Point:* "Raw rows don't tell a story. I engineered a 1-click visualization pipeline. From the in-memory ResultSet, SQBase automatically infers that we want a Bar Chart mapping customers to spending. I can export this as a PDF report for stakeholders with a single click."

### Step 5: Query Workspace & Diffs (1:30 - 2:00)
*Action:* Open the 'Query History' tab. Select a query from yesterday, and click 'Diff View' to compare it with the active tab.
*Talking Point:* "Every good developer relies on version control. I built a Git-style history system directly into the editor. You can instantly see where you broke the query using this side-by-side diff. All connections are also AES encrypted. Thanks for taking a look at SQBase."
