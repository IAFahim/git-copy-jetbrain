#!/bin/bash

# GitCopy IntelliJ Plugin Validation Script
echo "🔍 Validating git-copy IntelliJ Plugin Structure..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

ERRORS=0
WARNINGS=0

check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} Found: $1"
        return 0
    else
        echo -e "${RED}✗${NC} Missing: $1"
        ((ERRORS++))
        return 1
    fi
}

check_directory() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} Directory exists: $1"
        return 0
    else
        echo -e "${RED}✗${NC} Directory missing: $1"
        ((ERRORS++))
        return 1
    fi
}

echo "📁 Checking project structure..."
check_directory "src/main/kotlin/com/github/iafahim/gitcopyjetbrain"
check_directory "src/main/resources/META-INF"

echo "📝 Checking core files..."
check_file "src/main/resources/META-INF/plugin.xml"
check_file "src/main/resources/messages/MyBundle.properties"
check_file "build.gradle.kts"
check_file "gradle.properties"
check_file "README.md"

echo "🔧 Checking source files..."
check_file "src/main/kotlin/com/github/iafahim/gitcopyjetbrain/actions/GitCopyAction.kt"
check_file "src/main/kotlin/com/github/iafahim/gitcopyjetbrain/actions/GitCopyProjectAction.kt"
check_file "src/main/kotlin/com/github/iafahim/gitcopyjetbrain/services/GitCopyService.kt"
check_file "src/main/kotlin/com/github/iafahim/gitcopyjetbrain/services/GitCopyHistoryService.kt"
check_file "src/main/kotlin/com/github/iafahim/gitcopyjetbrain/settings/GitCopySettings.kt"
check_file "src/main/kotlin/com/github/iafahim/gitcopyjetbrain/settings/GitCopyConfigurable.kt"
check_file "src/main/kotlin/com/github/iafahim/gitcopyjetbrain/toolWindow/GitCopyToolWindowFactory.kt"
check_file "src/main/kotlin/com/github/iafahim/gitcopyjetbrain/ui/GitCopyDestinationDialog.kt"

echo "🔍 Checking plugin.xml configuration..."
if grep -q "GitCopyAction" src/main/resources/META-INF/plugin.xml; then
    echo -e "${GREEN}✓${NC} GitCopyAction registered"
else
    echo -e "${RED}✗${NC} GitCopyAction not found in plugin.xml"
    ((ERRORS++))
fi

if grep -q "GitCopyProjectAction" src/main/resources/META-INF/plugin.xml; then
    echo -e "${GREEN}✓${NC} GitCopyProjectAction registered"
else
    echo -e "${RED}✗${NC} GitCopyProjectAction not found in plugin.xml"
    ((ERRORS++))
fi

if grep -q "GitCopyConfigurable" src/main/resources/META-INF/plugin.xml; then
    echo -e "${GREEN}✓${NC} GitCopyConfigurable registered"
else
    echo -e "${RED}✗${NC} GitCopyConfigurable not found in plugin.xml"
    ((ERRORS++))
fi

if grep -q "GitCopyToolWindowFactory" src/main/resources/META-INF/plugin.xml; then
    echo -e "${GREEN}✓${NC} GitCopyToolWindowFactory registered"
else
    echo -e "${RED}✗${NC} GitCopyToolWindowFactory not found in plugin.xml"
    ((ERRORS++))
fi

echo "🎯 Checking Kotlin syntax..."
if command -v kotlinc &> /dev/null; then
    echo "Kotlin compiler found, checking syntax..."
    find src/main/kotlin -name "*.kt" | while read file; do
        if kotlinc -nowarn -Xskip-runtime-version-check -d /tmp/kotlin-check "$file" 2>&1 | grep -q "error"; then
            echo -e "${RED}✗${NC} Syntax error in: $file"
            ((ERRORS++))
        else
            echo -e "${GREEN}✓${NC} Valid syntax: $file"
        fi
    done
else
    echo -e "${YELLOW}⚠${NC} Kotlin compiler not found, skipping syntax check"
    ((WARNINGS++))
fi

echo "📋 Checking dependencies..."
if grep -q "org.jetbrains.kotlin" build.gradle.kts; then
    echo -e "${GREEN}✓${NC} Kotlin dependency found"
else
    echo -e "${RED}✗${NC} Kotlin dependency missing"
    ((ERRORS++))
fi

if grep -q "intelliJPlatform" build.gradle.kts; then
    echo -e "${GREEN}✓${NC} IntelliJ Platform dependency found"
else
    echo -e "${RED}✗${NC} IntelliJ Platform dependency missing"
    ((ERRORS++))
fi

echo ""
echo "📊 Summary:"
echo "Errors: $ERRORS"
echo "Warnings: $WARNINGS"

if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✓ Plugin structure validation PASSED${NC}"
    exit 0
else
    echo -e "${RED}✗ Plugin structure validation FAILED${NC}"
    exit 1
fi