@echo Uploading to github...
set /p commitMsg=Commit Message:
git add -A
git commit -m "%commitMsg%"
git push origin master
@echo Uploaded sucessfully!
pause 