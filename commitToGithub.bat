@echo Uploading to github...
set /p commitMsg=Commit Message:
git add *
git commit -m "%commitMsg%"
git push origin master
@echo Uploaded sucessfully!
