export default {
  async fetch(request, env) {
    if (request.method !== "POST") {
      return new Response("Sami AI is running", { status: 200 });
    }

    try {
      const { message } = await request.json();

      if (!message) {
        return Response.json({ error: "Message is required" }, { status: 400 });
      }

      const url =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=" +
        env.GEMINI_API_KEY;

      const response = await fetch(url, { signal: AbortSignal.timeout(30000),
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          contents: [
            {
              parts: [
                {
                  text:
                    "You are Sami, a helpful voice assistant. Reply in 1 short sentence, maximum 15 words. Reply in English.\n\nUser: " +
                    message
                }
              ]
            }
          ]
        })
      });

      const data = await response.json();

      if (!response.ok) {
        return Response.json(
          { error: data.error?.message || "Gemini request failed" },
          { status: 500 }
        );
      }

      const answer =
        data.candidates?.[0]?.content?.parts?.[0]?.text ||
        "Mujhe jawab nahi mila.";

      return Response.json({ answer });
    } catch (error) {
      return Response.json(
        { error: "Sami server error: " + error.message },
        { status: 500 }
      );
    }
  }
};
