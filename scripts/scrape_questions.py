#!/usr/bin/env python3
"""
JavaMentor Question Scraper + AI Converter
Uses MiniMax API to convert web content to MC questions
"""

import requests
import json
import re
import os
import sys
from typing import List, Dict, Any

# MiniMax API configuration
MINIMAX_API_KEY = os.environ.get("MINIMAX_API_KEY", "your-api-key-here")
MINIMAX_BASE_URL = "https://api.minimax.chat/v1"

def call_minimax(prompt: str, system_prompt: str = "You are a helpful assistant.") -> str:
    """Call MiniMax API to generate content"""
    headers = {
        "Authorization": f"Bearer {MINIMAX_API_KEY}",
        "Content-Type": "application/json"
    }
    
    payload = {
        "model": "MiniMax-M2.5",
        "messages": [
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": prompt}
        ],
        "temperature": 0.7
    }
    
    try:
        response = requests.post(
            f"{MINIMAX_BASE_URL}/text/chatcompletion_v2",
            headers=headers,
            json=payload,
            timeout=60
        )
        result = response.json()
        return result.get("choices", [{}])[0].get("message", {}).get("content", "")
    except Exception as e:
        print(f"API Error: {e}")
        return ""

def scrape_javabetter(url: str) -> str:
    """Scrape content from javabetter.cn"""
    headers = {
        "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36"
    }
    
    try:
        response = requests.get(url, headers=headers, timeout=30)
        response.encoding = 'utf-8'
        
        # Simple extraction - get all headings and paragraphs
        content = []
        
        # Remove scripts and styles
        html = re.sub(r'<script[^>]*>.*?</script>', '', response.text, flags=re.DOTALL)
        html = re.sub(r'<style[^>]*>.*?</style>', '', html, flags=re.DOTALL)
        
        # Extract text content
        text = re.sub(r'<[^>]+>', '\n', html)
        text = re.sub(r'\n+', '\n', text)
        text = text.strip()
        
        return text[:10000]  # Limit to 10k chars
    except Exception as e:
        print(f"Scraper Error: {e}")
        return ""

def convert_to_mc_questions(content: str, topic: str, count: int = 10) -> List[Dict]:
    """Use AI to convert content to MC questions"""
    
    system_prompt = """你係一個Java面試題目生成專家。根據提供既內容，生成高質量既4選1選擇題。

要求：
1. 每題要有難度標記 (1=簡單, 2=中等, 3=困難)
2. 每題要有正確答案 (A/B/C/D)
3. 每題要有詳細解釋 (起碼50字)
4. 每題要有tags
5. 用廣東話/繁體中文
6. 題目格式：
{"id": 1, "difficulty": 1, "multiSelect": false, "question": "問題內容", "optionA": "A選項", "optionB": "B選項", "optionC": "C選項", "optionD": "D選項", "correct": "A", "tags": "tag1,tag2", "explanation": "解釋內容"}

只返JSON array，唔好有其他野。"""

    prompt = f"""根據以下{topic}既內容，生成{count}道選擇題：

{content}

只返JSON array。"""

    result = call_minimax(prompt, system_prompt)
    
    # Parse JSON
    try:
        # Extract JSON array from response
        match = re.search(r'\[.*\]', result, re.DOTALL)
        if match:
            questions = json.loads(match.group())
            return questions
    except json.JSONDecodeError:
        print(f"JSON Parse Error: {result[:200]}")
    
    return []

def main():
    # URLs to scrape
    urls = [
        ("javase", "https://javabetter.cn/sidebar/sanfene/javase.html", 20),
        ("collection", "https://javabetter.cn/sidebar/sanfene/collection.html", 15),
        ("thread", "https://javabetter.cn/sidebar/sanfene/javathread.html", 15),
        ("jvm", "https://javabetter.cn/sidebar/sanfene/jvm.html", 15),
    ]
    
    all_topics = []
    
    for topic_id, url, count in urls:
        print(f"\n=== Scraping {topic_id} ===")
        
        # Scrape content
        content = scrape_javabetter(url)
        if not content:
            print(f"Failed to scrape {url}")
            continue
            
        print(f"Scraped {len(content)} chars")
        
        # Convert to MC questions
        questions = convert_to_mc_questions(content, topic_id, count)
        print(f"Generated {len(questions)} questions")
        
        if questions:
            topic = {
                "topicId": topic_id,
                "name": topic_id.title(),
                "description": f"{topic_id} questions",
                "questions": questions
            }
            all_topics.append(topic)
    
    # Save to JSON
    output = {"topics": all_topics}
    
    with open("output_questions.json", "w", encoding="utf-8") as f:
        json.dump(output, f, ensure_ascii=False, indent=2)
    
    print(f"\n=== Done! Generated {sum(len(t['questions']) for t in all_topics)} questions ===")

if __name__ == "__main__":
    main()
